package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback

private const val FOV = 67f
private const val SPEED = 8.25f

class Player {

	var position: Vector3 = Vector3(8f, 16f, 8f)
	private var isCameraDirty = false

	private var isCapturingInput = true

	init {
		Gdx.input.isCursorCatched = true
	}

	val camera: PerspectiveCamera = PerspectiveCamera(FOV, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()).apply {
		position.set(this@Player.position)
		near = 1f
		far = 300f
		update()
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
		val mouseX = -Gdx.input.getDeltaX(0)
		val mouseY = -Gdx.input.getDeltaY(0)

		if (mouseX != 0) {
			this.camera.direction.rotate(camera.up, mouseX * 1.25f)
			isCameraDirty = true
		}

		if (mouseY != 0) {
			val currentAngle = this.camera.direction.y
			if (!(currentAngle < -0.98 && mouseY < 0) && !(currentAngle > 0.98 && mouseY > 0)) {
				this.camera.direction.rotate(camera.direction.cpy().crs(camera.up).nor(), mouseY * 1.25f)
				isCameraDirty = true
			}
		}

		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			val origin = this.camera.position.cpy()
			val target = origin.add(this.camera.direction.cpy().scl(50f))

			val callback = ClosestRayResultCallback(origin, target)
			callback.collisionObject = null
			callback.closestHitFraction = 1f
		}
	}

	private fun handleKeyInput() {
		val speed = SPEED * Gdx.graphics.deltaTime

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
			translateCamera(this.camera.direction.cpy().crs(camera.up).nor().scl(-speed))
		}

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			translateCamera(this.camera.direction.cpy().crs(camera.up).nor().scl(speed))
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			translateCamera(this.camera.direction.cpy().scl(speed))
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
			translateCamera(this.camera.direction.cpy().scl(-speed))
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			translateCamera(this.camera.up.cpy().scl(speed))
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			translateCamera(this.camera.up.cpy().scl(-speed))
		}
	}

	fun update() {
		if (this.isCapturingInput) {
			this.handleMouseInput()
			this.handleKeyInput()
		} else {
			this.handleInputCapture()
		}

		if(isCameraDirty) {
			this.camera.update()
			isCameraDirty = false
		}
	}
	
	fun dispose() {
		
	}

}