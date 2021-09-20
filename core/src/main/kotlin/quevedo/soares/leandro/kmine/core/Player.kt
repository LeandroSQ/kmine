package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import ktx.math.minus
import ktx.math.plus
import ktx.math.times
import ktx.math.vec3
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.utils.*

private const val FOV = 67f
private const val SPEED = 8.25f
private const val MOUSE_SENSITIVITY = 64.1f
private const val MAX_CAMERA_PITCH = 0.987f
private const val GRAVITY_FORCE = 9.80665f
private const val AIR_FRICTION = 0.9f
private const val JUMP_FORCE = GRAVITY_FORCE * 2f

class Player {

	val height = 2f

	var isFlying = true
	var isGrounded = false

	var speed: Vector3 = Vector3.Zero

	var position: Vector3
		get() = this.camera.position
		set(value) { camera.position.set(value) }

	private var isCameraDirty = false

	private var isCapturingInput = true

	lateinit var camera: PerspectiveCamera
		private set

	private lateinit var debugCube: ModelInstance

	fun onCreate() {
		Gdx.input.isCursorCatched = true

		this.camera = PerspectiveCamera(FOV, Gdx.graphics.width * Gdx.graphics.density, Gdx.graphics.height * Gdx.graphics.density).apply {
			near = 0.5f
			far = 300f
			update()
		}

		val chunk = Game.world.terrain.chunks.random()
		chunk.getHighest(chunk.width / 2, chunk.depth / 2)?.position?.cpy()?.let {
			this.position = it + Vector3.Y * 2
		}

		this.debugCube = Gizmo.box(this.position, vec3(1f, 1f, 1f), Color(1f, 1f, 0f, 0.2f), filled = true)
	}

	private fun getChunkBasedOnPosition(): Chunk? {
		return Game.world.terrain.getChunkAt(this.position.x, this.position.z)
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

		// TODO: Cube ray casting
		/*if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			val origin = this.camera.position.cpy()
			val target = origin + (this.camera.direction.cpy() * 50f)

			val callback = ClosestRayResultCallback(origin, target)
			callback.collisionObject = null
			callback.closestHitFraction = 1f
		}*/
	}

	private fun handleKeyInput() {
		val speed = SPEED * Gdx.graphics.deltaTime * if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) 3f else 1f

		if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
			this.isFlying = !this.isFlying
		}

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
			translateCamera(this.camera.direction.cpy().crs(camera.up).nor() * -speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			translateCamera(this.camera.direction.cpy().crs(camera.up).nor() * speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			if (!this.isFlying) {
				this.getChunkBasedOnPosition()?.let { chunk ->
					val relativePosition = this.position - chunk.position
					val cubesBlockingWalkDirection = chunk.isEmpty(relativePosition.xInt, relativePosition.yInt + 1, relativePosition.zInt + 1) && chunk.isEmpty(relativePosition.xInt, relativePosition.yInt + 2, relativePosition.zInt + 1)
					if (cubesBlockingWalkDirection) translateCamera(this.camera.direction.cpy() * speed)
				} ?: run {
					translateCamera(this.camera.direction.cpy() * speed)
				}
			} else {
				translateCamera(this.camera.direction.cpy() * speed)
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
			translateCamera(this.camera.direction.cpy() * -speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			if (!this.isFlying && this.isGrounded) {
				this.speed.y += JUMP_FORCE
				this.isGrounded = false
			} else if (this.isFlying) {
				translateCamera(this.camera.up.cpy() * speed)
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
			translateCamera(this.camera.up.cpy() * -speed)
		}
	}

	private fun handlePhysics() {
		if (!this.isGrounded) {
			this.position.y -= GRAVITY_FORCE * Gdx.graphics.deltaTime
			this.position.y += this.speed.y * Gdx.graphics.deltaTime
			this.speed.y *= AIR_FRICTION

			this.isCameraDirty = true
		}

		// 1- Find the chunk of which the player is in
		val chunk = this.getChunkBasedOnPosition() ?: return

		// 2- Get the surface cube at the player position x and z
		val relativePosition = this.position - chunk.position
		val cube = chunk.getHighest(relativePosition.xInt, relativePosition.zInt, yStart = relativePosition.yInt + 1) ?: return

		// 3- Collision response
		val min = cube.position.y + chunk.position.y + cube.size + this.height
		if (this.position.y <= min) {
			this.position.y = min
			this.isGrounded = true
			this.isCameraDirty = true
		} else {
			this.isGrounded = false
		}
	}

	fun update() {
		if (!this.isFlying) this.handlePhysics()

		if (this.isCapturingInput) {
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

	}

	fun onResize(width: Float, height: Float) {
		this.camera.viewportWidth = width
		this.camera.viewportHeight = height
		this.camera.update()
	}

}