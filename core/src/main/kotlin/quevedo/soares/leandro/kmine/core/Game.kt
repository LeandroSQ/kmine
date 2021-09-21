package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.HdpiUtils
import com.badlogic.gdx.physics.bullet.Bullet

private val ANTIALIASING by lazy { if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0 }

object Game : ApplicationAdapter() {

    var hud = HUD()
        private set

    var world = World()
        private set

    var player = Player()
        private set

    var physics = Physics()

    var isInDebugMode = true

    override fun create() {
        Bullet.init()

        this.physics.onCreate()
        this.world.onCreate()
        this.player.onCreate()
        this.hud.onCreate()
    }

    private fun handleKeyInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            this.isInDebugMode = !this.isInDebugMode
        }
    }

    override fun render() {
        HdpiUtils.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or ANTIALIASING)

        this.handleKeyInput()

        this.physics.update()
        this.player.update()

        this.world.render()
        this.hud.render()
    }

    override fun dispose() {
        this.hud.dispose()
        this.world.dispose()
        this.player.dispose()
        this.physics.dispose()
    }

    override fun resize(width: Int, height: Int) {
        val w = HdpiUtils.toBackBufferX(width).toFloat()
        val h = HdpiUtils.toBackBufferY(height).toFloat()

        this.hud.onResize(w, h)
        this.player.onResize(w, h)
    }

}