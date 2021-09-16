package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.bullet.Bullet

val ANTIALIASING by lazy { if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0 }

class Game : ApplicationAdapter() {

    private lateinit var font: BitmapFont
    private lateinit var hudBatch: SpriteBatch
    private lateinit var hudCamera: OrthographicCamera

    private var world = World()
    private lateinit var player: Player

    override fun create() {
        Bullet.init()

        this.player = Player()
        this.world.create()

        this.setupHUD()
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT/* or ANTIALIASING*/)
//        Gdx.gl.glEnable(GL20.GL_CULL_FACE)
//        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
//        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL)

        this.player.update()
        this.world.render(this.player.camera)

        this.renderHUD()
    }

    private fun renderHUD() {
        this.hudCamera.update()
        this.hudBatch.projectionMatrix = this.hudCamera.combined
        this.hudBatch.begin()
        this.font.draw(this.hudBatch, "FPS: ${Gdx.graphics.framesPerSecond}", 0f, this.font.lineHeight)
        this.hudBatch.end()
    }

    override fun dispose() {
        this.hudBatch.dispose()
        this.world.dispose()
        this.player.dispose()
        this.font.dispose()
    }

}