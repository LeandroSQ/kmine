package quevedo.soares.leandro.kmine.core.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import ktx.math.div
import ktx.math.minus
import ktx.math.plus
import ktx.math.times
import quevedo.soares.leandro.kmine.core.Game
import quevedo.soares.leandro.kmine.core.models.PlayerPhysicsProperties
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.type.TorchCube
import quevedo.soares.leandro.kmine.core.utils.position

internal const val AIR_FRICTION = 0.7f

class PhysicsPlayerController(private val player: Player) : BasePlayerController() {

	lateinit var physics: PlayerPhysicsProperties
		private set

	private var velocity = Vector3(0f, 0f, 0f)

	override fun create() {
		super.create()

		this.physics = PlayerPhysicsProperties(this.player.dimensions, this.player.position).apply {
			Game.physics.add(this)
		}
	}

	private fun onPlaceCube(position: Vector3) {
		Game.world.terrain.getChunkAt(position.x, position.z)?.let { chunk ->
			val relativeHitPosition = position - chunk.position
			chunk.set(relativeHitPosition, Chunk.EMPTY)
			Game.world.terrain.update()
		}
	}

	private fun onBreakCube(position: Vector3) {
		Game.world.terrain.getChunkAt(position.x, position.z)?.let { chunk ->
			val relativeHitPosition = position - chunk.position
			chunk.set(relativeHitPosition, TorchCube())
			Game.world.terrain.update()
		}
	}

	private fun handleRayCastInput() {
		val pickRay = this.player.camera.getPickRay(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f)
		val from = pickRay.origin
		val to = pickRay.direction.scl(5f).add(from)

		val hit = Game.physics.castRay(from, to)

		if (hit != null) {
			this.player.cubeOutline.transform.position = hit.negative
			this.player.isCubeOutlineVisible = true

			if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
				onPlaceCube(hit.negative)
			} else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
				onBreakCube(hit.positive)
			}
		} else {
			this.player.isCubeOutlineVisible = false
		}
	}

	private fun handleMouseInput() {
		val sensitivity = MOUSE_SENSITIVITY * Gdx.graphics.deltaTime
		val mouseX = -Gdx.input.getDeltaX(0)
		val mouseY = -Gdx.input.getDeltaY(0)

		if (mouseX != 0) {
			this.player.camera.direction.rotate(this.player.camera.up, mouseX.toFloat() * sensitivity)
			this.player.isCameraDirty = true
		}

		if (mouseY != 0) {
			val currentAngle = this.player.camera.direction.y
			if (!(currentAngle < -MAX_CAMERA_PITCH && mouseY < 0) && !(currentAngle > MAX_CAMERA_PITCH && mouseY > 0)) {
				val axis = this.player.camera.direction.cpy().crs(this.player.camera.up).nor()
				this.player.camera.direction.rotate(axis, mouseY.toFloat() * sensitivity)
				this.player.isCameraDirty = true
			}
		}
	}

	private fun translate(direction: Vector3) {
		direction.y = 0f
		this.velocity += direction
	}

	private fun handleMovementInput() {
		val speedMultiplier = if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) 1.5f else 1f
		val speed = SPEED * Gdx.graphics.deltaTime * speedMultiplier

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			this.player.isCameraDirty = true
			this.translate(this.player.camera.direction.cpy().crs(this.player.camera.up).nor() * -speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			this.player.isCameraDirty = true
			this.translate(this.player.camera.direction.cpy().crs(this.player.camera.up).nor() * speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			this.player.isCameraDirty = true
			this.translate(this.player.camera.direction.cpy() * speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
			this.player.isCameraDirty = true
			this.translate(this.player.camera.direction.cpy() * -speed)
		}

		if (this.physics.controller.canJump() && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			this.player.isCameraDirty = true
			this.velocity += this.player.camera.up * AIR_FRICTION
		}
	}

	override fun activate() {
		this.physics.ghostObject.worldTransform = Matrix4().apply {
			setTranslation(player.position)
		}
	}

	override fun update() {
		// Check if the user has moved
		if (!this.player.position.epsilonEquals(physics.ghostObject.worldTransform.position)) {
			this.player.isCameraDirty = true

			// Sets the player position, translating the camera to player.dimension.y
			this.player.position = physics.ghostObject.worldTransform.position.cpy() + this.player.camera.up * this.player.dimensions.y / 1.5f
		}

		// Handles input
		if (Game.isCapturingInput) {
			// Apply air friction
			this.velocity.x = 0f
			this.velocity.y *= AIR_FRICTION
			this.velocity.z = 0f

			this.handleRayCastInput()
			this.handleMovementInput()
			this.handleMouseInput()

			// Sets the kinematic controller to walk
			this.physics.controller.setWalkDirection(this.velocity)
		}
	}

	override fun dispose() {
		this.physics.dispose()
	}

}