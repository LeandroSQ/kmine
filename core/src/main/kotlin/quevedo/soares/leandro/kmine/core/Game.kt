package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.bullet.Bullet

val ANTIALIASING by lazy { if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0 }

class Game : ApplicationAdapter() {

    private var hud = HUDController()
    private var world = World()
    private lateinit var player: Player

    override fun create() {
        Bullet.init()

        this.player = Player()
        this.world.create()
        this.hud.create()
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or ANTIALIASING)
//        Gdx.gl.glEnable(GL20.GL_CULL_FACE)
//        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
//        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL)

        this.player.update()

        this.world.render(this.player.camera)
        Gdx.gl20.glEnable(GL20.GL_BLEND)
        Gdx.gl20.glBlendFunc(GL20.GL_ONE_MINUS_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR)
        this.hud.render()
        Gdx.gl20.glDisable(GL20.GL_BLEND)
    }

    override fun dispose() {
        this.hud.dispose()
        this.world.dispose()
        this.player.dispose()
    }

    override fun resize(width: Int, height: Int) {
        this.hud.onResize(width.toFloat(), height.toFloat())
        this.player.onResize(width.toFloat(), height.toFloat())
    }

}