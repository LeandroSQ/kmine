package quevedo.soares.leandro.kmine.core.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import ktx.math.times
import quevedo.soares.leandro.kmine.core.Game

class FlyingPlayerController(private val player: Player) : BasePlayerController() {

	private fun translateCamera(direction: Vector3) {
		this.player.camera.translate(direction)
		this.player.isCameraDirty = true
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
				this.player.camera.direction.rotate(this.player.camera.direction.cpy().crs(this.player.camera.up).nor(), mouseY.toFloat() * sensitivity)
				this.player.isCameraDirty = true
			}
		}
	}

	private fun handleMovementInput() {
		val speedMultiplier = if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) 6f else 2f
		val speed = SPEED * Gdx.graphics.deltaTime * speedMultiplier

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			this.player.camera.direction.rotate(this.player.camera.up, (speed * Math.PI * 3).toFloat())
			this.player.isCameraDirty = true
		}

		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			this.player.camera.direction.rotate(this.player.camera.up, -(speed * Math.PI * 3).toFloat())
			this.player.isCameraDirty = true
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			this.player.camera.rotate(this.player.camera.direction.cpy().crs(this.player.camera.up).nor(), (speed * Math.PI * 3).toFloat())
			this.player.isCameraDirty = true
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			this.player.camera.rotate(this.player.camera.direction.cpy().crs(this.player.camera.up).nor(), -(speed * Math.PI * 3).toFloat())
			this.player.isCameraDirty = true
		}

		if (Gdx.input.isKeyPressed(Input.Keys.A))
			translateCamera(this.player.camera.direction.cpy().crs(this.player.camera.up).nor() * -speed)

		if (Gdx.input.isKeyPressed(Input.Keys.D))
			translateCamera(this.player.camera.direction.cpy().crs(this.player.camera.up).nor() * speed)

		if (Gdx.input.isKeyPressed(Input.Keys.W))
			translateCamera(this.player.camera.direction.cpy() * speed)

		if (Gdx.input.isKeyPressed(Input.Keys.S))
			translateCamera(this.player.camera.direction.cpy() * -speed)

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
			translateCamera(this.player.camera.up.cpy() * speed)

		if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT))
			translateCamera(this.player.camera.up.cpy() * -speed)
	}

	override fun activate() {
		this.player.isCubeOutlineVisible = false
	}

	override fun update() {
		if (Game.isCapturingInput) {
			this.handleMovementInput()
			this.handleMouseInput()
		}
	}

}