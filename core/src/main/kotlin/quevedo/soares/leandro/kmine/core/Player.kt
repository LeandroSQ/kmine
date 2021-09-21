package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.MathUtils.floor
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import ktx.math.div
import ktx.math.minus
import ktx.math.vec3
import ktx.math.plus
import ktx.math.times
import quevedo.soares.leandro.kmine.core.models.PlayerPhysicsProperties
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.type.TorchCube
import quevedo.soares.leandro.kmine.core.utils.*

private const val FOV = 67f
private const val SPEED = 4.25f
private const val MOUSE_SENSITIVITY = 64.1f
private const val MAX_CAMERA_PITCH = 0.987f

class Player {

	lateinit var physics: PlayerPhysicsProperties
		private set

	val dimensions = vec3(1, 2, 1)

	var velocity = Vector3(0f, 0f, 0f)
	var position: Vector3
		get() = this.camera.position
		set(value) {
			camera.position.set(value)
		}

	val transform: Matrix4
		get() = this.camera.projection

	private var isCameraDirty = false

	private var isCapturingInput = true

	lateinit var camera: PerspectiveCamera
		private set

	lateinit var cubeOutline : ModelInstance

	fun onCreate() {
		Gdx.input.isCursorCatched = true

		// Creates the camera
		this.camera = PerspectiveCamera(FOV, Gdx.graphics.width * Gdx.graphics.density, Gdx.graphics.height * Gdx.graphics.density).apply {
			near = 0.1f
			far = 300f
			update()
		}

		// Positions the player
		val chunk = Game.world.terrain.chunks.random()
		chunk.getHighest(floor(chunk.width / 2f), floor(chunk.depth / 2f))?.let {
			this.position = chunk.position + it.position + Vector3.Y * dimensions.y * 10
		}

		this.setupPhysicsProperties()

		cubeOutline = Gizmo.box(Vector3(0f, 0f, 0f), vec3(1.05f, 1.05f,1.05f), Color.BLACK, filled = false)
	}

	private fun setupPhysicsProperties() {
		this.physics = PlayerPhysicsProperties(this.dimensions, this.position).apply {
			Game.physics.add(this)
		}
	}

	private fun translateCamera(direction: Vector3) {
		this.camera.translate(direction)

		isCameraDirty = true
	}

	private fun handleInputCapture() {
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			this.isCapturingInput = true
			Gdx.input.isCursorCatched = true
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit()
		}
	}

	private fun onPlaceCube(position: Vector3) {
		Game.world.terrain.getChunkAt(position.x, position.z)?.let { chunk ->
			val relativeHitPosition = position - chunk.position
			chunk.set(relativeHitPosition, Chunk.EMPTY)
		}
	}

	private fun onBreakCube(position: Vector3) {
		Game.world.terrain.getChunkAt(position.x, position.z)?.let { chunk ->
			val relativeHitPosition = position - chunk.position
			chunk.set(relativeHitPosition, TorchCube())
		}
	}

	private fun handleRayCasting() {
		val pickRay = this.camera.getPickRay(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f)
		val from = pickRay.origin
		val to = pickRay.direction.scl(5f).add(from)

		val hit = Game.physics.castRay(from, to)

		if (hit != null) {
			this.cubeOutline.transform.position = hit.negative

			if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
				onPlaceCube(hit.negative)
			} else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
				onBreakCube(hit.positive)
			}
		} else {
			cubeOutline.transform.position = vec3(0f, 0f, 0f)
		}
	}

	private fun handleMouseInput() {
		val sensitivity = MOUSE_SENSITIVITY * Gdx.graphics.deltaTime
		val mouseX = -Gdx.input.getDeltaX(0)
		val mouseY = -Gdx.input.getDeltaY(0)

		if (mouseX != 0) {
			this.camera.direction.rotate(camera.up, mouseX.toFloat() * sensitivity)
			isCameraDirty = true
		}

		if (mouseY != 0) {
			val currentAngle = this.camera.direction.y
			if (!(currentAngle < -MAX_CAMERA_PITCH && mouseY < 0) && !(currentAngle > MAX_CAMERA_PITCH && mouseY > 0)) {
				this.camera.direction.rotate(this.camera.direction.cpy().crs(camera.up).nor(), mouseY.toFloat() * sensitivity)
				isCameraDirty = true
			}
		}
	}

	private fun handleKeyInput() {
		val speedMultiplier = if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) 3f else 1f
		val speed = SPEED * Gdx.graphics.deltaTime * speedMultiplier
		this.velocity = Vector3(0f, 0f, 0f)

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Gdx.input.isCursorCatched = false
			isCapturingInput = false
		}

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			this.camera.rotate(Vector3.Y, (speed * Math.PI * 3).toFloat())
			isCameraDirty = true
		}

		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			this.camera.rotate(Vector3.Y, -(speed * Math.PI * 3).toFloat())
			isCameraDirty = true
		}

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			this.velocity += camera.direction.cpy().crs(camera.up).nor() * -speed * vec3(1, 0, 1)
			isCameraDirty = true
			//translateCamera(this.camera.direction.cpy().crs(camera.up).nor() * -speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			this.velocity += camera.direction.cpy().crs(camera.up).nor() * speed * vec3(1, 0, 1)
			isCameraDirty = true
			//translateCamera(this.camera.direction.cpy().crs(camera.up).nor() * speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			this.velocity += this.camera.direction.cpy() * speed * vec3(1, 0, 1)
			isCameraDirty = true
			//translateCamera(this.camera.direction.cpy() * speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
			this.velocity += this.camera.direction.cpy() * -speed * vec3(1, 0, 1)
			isCameraDirty = true
			//translateCamera(this.camera.direction.cpy() * -speed)
		}

		if (this.physics.controller.canJump() && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			this.velocity += this.camera.up
			//translateCamera(this.camera.up.cpy() * speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
			translateCamera(this.camera.up.cpy() * -speed)
		}
	}

	fun update() {
		if (!this.position.epsilonEquals(physics.ghostObject.worldTransform.position)) {
			isCameraDirty = true
			this.position = physics.ghostObject.worldTransform.position + camera.up * dimensions.y / 1.5f
		}

		this.physics.controller.setWalkDirection(this.velocity)

		if (this.isCapturingInput) {
			this.handleRayCasting()
			this.handleMouseInput()

			this.handleKeyInput()
		} else {
			this.handleInputCapture()
		}

		if (isCameraDirty) {
			this.camera.update()
			isCameraDirty = false
		}
	}

	fun dispose() {
		this.physics.dispose()
	}

	fun onResize(width: Float, height: Float) {
		this.camera.viewportWidth = width
		this.camera.viewportHeight = height
		this.camera.update()
	}

}