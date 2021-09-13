package quevedo.soares.leandro.kmine

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController

val ANTIALIASING by lazy { if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0 }

class Game : ApplicationAdapter() {

    private lateinit var font: BitmapFont
    private lateinit var hudBatch: SpriteBatch
    private lateinit var hudCamera: OrthographicCamera

    private lateinit var gameCamera: PerspectiveCamera
    private lateinit var cameraController: CameraInputController

    private var world = World()

    override fun create() {
        this.setupGameCamera()
        this.world.create()
        this.setupHUD()
    }

    private fun setupGameCamera() {
        this.gameCamera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()).apply {
            position.set(10f, 10f, 10f)
            lookAt(0f, 0f, 0f)
            near = 1f
            far = 300f
            update()
        }

        this.cameraController = CameraInputController(this.gameCamera)
        Gdx.input.inputProcessor = this.cameraController
    }

    private fun setupHUD() {
        this.hudCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()).apply {
            position.set(viewportWidth / 2f, viewportHeight / 2f, 1f)
        }
        this.font = BitmapFont(Gdx.files.local("default.fnt"))
        this.font.color = Color.WHITE
        this.hudBatch = SpriteBatch()
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or ANTIALIASING)

        this.cameraController.update()

        this.world.render(this.gameCamera)

        this.renderHUD()
    }

    private fun renderHUD() {
        this.hudCamera.update()
        this.hudBatch.projectionMatrix = this.hudCamera.combined
        this.hudBatch.begin()
        this.font.draw(this.hudBatch, "FPS ${Gdx.graphics.framesPerSecond}", 0f, this.font.lineHeight)
        this.hudBatch.end()
    }

    override fun dispose() {
        this.hudBatch.dispose()
        this.world.dispose()
        this.font.dispose()
    }

}