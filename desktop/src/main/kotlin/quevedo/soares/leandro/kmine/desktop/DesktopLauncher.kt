package quevedo.soares.leandro.kmine.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.HdpiMode
import quevedo.soares.leandro.kmine.core.Game


object DesktopLauncher {

	/**
	 * workaround for Mac
	 */
	/*private fun setApplicationIcon() {
		try {
			val cls = Class.forName("com.apple.eawt.Application")
			val application = cls.newInstance().javaClass.getMethod("getApplication").invoke(null)
			val icon = Gdx.files.local("icons/icon.png")
			application.javaClass.getMethod("setDockIconImage", Image::class.java)
				.invoke(application, ImageIcon(icon.file().absolutePath).image)
		} catch (e: Exception) {
			// nobody cares!
		}
	}*/

	@JvmStatic
	fun main(args: Array<String>) {
		Lwjgl3Application(Game(), Lwjgl3ApplicationConfiguration().apply {
			setTitle("KMine")
			setWindowSizeLimits(640, 580, -1, -1)
			setHdpiMode(HdpiMode.Logical)
			useVsync(false)
			setForegroundFPS(120)
			setInitialBackgroundColor(Color(60 / 255f, 174 / 255f, 243 / 255f, 1f))
			setWindowIcon("icons/icon128.png", "icons/icon64.png", "icons/icon32.png", "icons/icon16.png")
		})
	}

}
