package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.HdpiUtils
import com.badlogic.gdx.physics.bullet.Bullet
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

private val ANTIALIASING by lazy { if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0 }

object Game : ApplicationAdapter() {

	var hud = HUD()
		private set

	var world = World()
		private set

	var player = Player()
		private set

	var physics = Physics()

	private val eventQueue = LinkedBlockingQueue<() -> Unit>()

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

	private fun handleEventQueue() {
		while(eventQueue.isNotEmpty()) {
			try {
				eventQueue.take().invoke()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	override fun render() {
		val start = System.currentTimeMillis()

		HdpiUtils.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or ANTIALIASING)

		this.handleEventQueue()

		this.handleKeyInput()

		this.physics.update()
		this.player.update()

		this.world.render()
		this.hud.render()

		val end = System.currentTimeMillis()
		val frameTime = (end - start).toShort()
		this.hud.frameTimeQueue.push(frameTime)
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

	fun postInvoke(event: () -> Unit) {
		this.eventQueue.add(event)
	}

}