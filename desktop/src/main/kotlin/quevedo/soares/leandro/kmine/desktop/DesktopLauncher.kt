package quevedo.soares.leandro.kmine.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.HdpiMode
import quevedo.soares.leandro.kmine.core.Game

object DesktopLauncher {

	@JvmStatic
	fun main(args: Array<String>) {
		Lwjgl3Application(Game, Lwjgl3ApplicationConfiguration().apply {
			setTitle("KMine")
			setWindowSizeLimits(640, 580, -1, -1)
			setHdpiMode(HdpiMode.Logical)
			useVsync(true)
			setForegroundFPS(0)
			setInitialBackgroundColor(Color(60 / 255f, 174 / 255f, 243 / 255f, 0f))
			setWindowIcon("icons/icon128.png", "icons/icon64.png", "icons/icon32.png", "icons/icon16.png")
		})
	}

}
