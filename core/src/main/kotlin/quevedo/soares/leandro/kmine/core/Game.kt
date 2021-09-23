package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.HdpiUtils
import com.badlogic.gdx.physics.bullet.Bullet
import quevedo.soares.leandro.kmine.core.player.Player
import quevedo.soares.leandro.kmine.core.terrain.Cube

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
	var isCapturingInput = true
		private set

	override fun create() {
		Bullet.init()
		Cube.loadTextures()

		this.physics.onCreate()
		this.world.onCreate()
		this.player.onCreate()
		this.hud.onCreate()
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

	private fun handleKeyInput() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Gdx.input.isCursorCatched = false
			isCapturingInput = false
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
			this.isInDebugMode = !this.isInDebugMode
		}
	}

	override fun render() {
		val start = System.currentTimeMillis()

		HdpiUtils.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or ANTIALIASING)

		if (isCapturingInput) this.handleKeyInput()
		else this.handleInputCapture()

		this.physics.update()
		this.player.update()

		this.world.render()
		this.hud.render()

		if(isInDebugMode) {
			val end = System.currentTimeMillis()
			val frameTime = (end - start).toShort()
			this.hud.frameTimeQueue.push(frameTime)
		}
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