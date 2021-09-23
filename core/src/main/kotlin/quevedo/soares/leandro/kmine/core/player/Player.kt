package quevedo.soares.leandro.kmine.core.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils.floor
import com.badlogic.gdx.math.Vector3
import ktx.math.plus
import ktx.math.times
import ktx.math.vec3
import quevedo.soares.leandro.kmine.core.Game
import quevedo.soares.leandro.kmine.core.utils.vec3

private const val FOV = 67f
internal const val SPEED = 4.25f
internal const val MOUSE_SENSITIVITY = 64.1f
internal const val MAX_CAMERA_PITCH = 0.987f

class Player {

	val dimensions = vec3(1, 2, 1)

	var position: Vector3
		get() = this.camera.position
		set(value) {
			camera.position.set(value)
		}

	internal var isCameraDirty = false

	lateinit var camera: PerspectiveCamera
		private set

	internal lateinit var cubeOutline: ModelInstance
	internal var isCubeOutlineVisible = false

	private var isFlying = true
	private val controller: BasePlayerController get() = if (this.isFlying) flyingController else physicsController
	private var flyingController = FlyingPlayerController(this)
	private var physicsController = PhysicsPlayerController(this)

	fun onCreate() {
		Gdx.input.isCursorCatched = true

		this.setupCamera()
		this.generateInitialPosition()
		this.setupCubeOutline()
		this.physicsController.create()
		this.flyingController.create()
	}

	private fun setupCubeOutline() {
		val size = vec3(1.05f, 1.05f, 1.05f)
		val material = Material(ColorAttribute.createDiffuse(Color.BLACK))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.ColorPacked).toLong()
		val model = ModelBuilder().createBox(size.x, size.y, size.z, GL20.GL_LINES, material, attributes)

		cubeOutline = ModelInstance(model)
	}

	private fun setupCamera() {
		// Creates the camera
		this.camera = PerspectiveCamera(FOV, Gdx.graphics.width * Gdx.graphics.density, Gdx.graphics.height * Gdx.graphics.density).apply {
			near = 0.1f
			far = 300f
			update()
		}
	}

	private fun generateInitialPosition() {
		// Positions the player
		this.position = Vector3.Y * dimensions.y + 10
		val chunk = Game.world.terrain.chunks.random()
		chunk.getHighest(floor(chunk.width / 2f), floor(chunk.depth / 2f))?.let {
			this.position = chunk.position + it.position + Vector3.Y * dimensions.y * 10
		}
	}

	private fun handleInput() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
			this.isFlying = !this.isFlying
			this.controller.activate()
		}
	}

	fun update() {
		isCameraDirty = false
		controller.update()

		if (Game.isCapturingInput) {
			handleInput()
		}

		if (isCameraDirty) {
			camera.update()
		}
	}

	fun render(modelBatch: ModelBatch, environment: Environment) {
		if (this.isCubeOutlineVisible) modelBatch.render(this.cubeOutline, environment)
	}

	fun dispose() {
		this.cubeOutline.model.dispose()
		this.flyingController.dispose()
		this.physicsController.dispose()
	}

	fun onResize(width: Float, height: Float) {
		this.camera.viewportWidth = width
		this.camera.viewportHeight = height
		this.camera.update()
	}

}