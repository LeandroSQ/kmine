package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import ktx.math.times
import ktx.math.vec3

private const val FOV = 67f
private const val SPEED = 8.25f
private const val MOUSE_SENSITIVITY = 64f
private const val MAX_CAMERA_PITCH = 0.987f

class Player {

	var position: Vector3 = Vector3(8f, 16f, 8f)
	private var isCameraDirty = false

	private var isCapturingInput = true

	val camera: PerspectiveCamera = PerspectiveCamera(FOV, Gdx.graphics.width * Gdx.graphics.density, Gdx.graphics.height * Gdx.graphics.density).apply {
		position.set(this@Player.position)
		near = 1f
		far = 300f
		update()
	}

	init {
		Gdx.input.isCursorCatched = true
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
			this.camera.direction.rotate(camera.up, mouseX * sensitivity)
			isCameraDirty = true
		}

		if (mouseY != 0) {
			val currentAngle = this.camera.direction.y
			if (!(currentAngle < -MAX_CAMERA_PITCH && mouseY < 0) && !(currentAngle > MAX_CAMERA_PITCH && mouseY > 0)) {
				this.camera.direction.rotate(this.camera.direction.cpy().crs(camera.up).nor(), mouseY * sensitivity)
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
			translateCamera(this.camera.direction.cpy().crs(camera.up).nor() * -speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			translateCamera(this.camera.direction.cpy().crs(camera.up).nor() * speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			translateCamera(this.camera.direction.cpy() * speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
			translateCamera(this.camera.direction.cpy() * -speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			translateCamera(this.camera.up.cpy() * speed)
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			translateCamera(this.camera.up.cpy() * -speed)
		}
	}

	private fun lerp(a: Vector3, b: Vector3, step: Float): Vector3 {
		fun f(current: Float, target: Float): Float {
			val distance = target - current

			val x = current + if (distance > step) step else if (distance < -step) -step else distance
			if (x > 1f) println(x)
			return x
		}

		return vec3(f(a.x, b.x), f(a.y, b.y), f(a.z, b.z))
	}

	fun update() {
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