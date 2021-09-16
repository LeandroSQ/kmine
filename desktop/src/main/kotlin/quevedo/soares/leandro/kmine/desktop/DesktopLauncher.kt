package quevedo.soares.leandro.kmine.desktop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.HdpiMode
import quevedo.soares.leandro.kmine.core.Game
import java.awt.Image
import javax.swing.ImageIcon


object DesktopLauncher {

    /**
     * workaround for Mac
     */
    private fun setApplicationIcon() {
        try {
            val cls = Class.forName("com.apple.eawt.Application")
            val application = cls.newInstance().javaClass.getMethod("getApplication").invoke(null)
            val icon = Gdx.files.local("icons/icon.png")
            application.javaClass.getMethod("setDockIconImage", Image::class.java)
                .invoke(application, ImageIcon(icon.file().absolutePath).image)
        } catch (e: Exception) {
            // nobody cares!
        }
    }

    private fun setDockIconForMac() {
        try {
            val app = Class.forName("com.apple.eawt.Application")
            val instance = app.newInstance().javaClass.getMethod("getApplication").invoke(null)
            val icon = Gdx.files.local("icon128.png")
            instance.javaClass.getMethod("setDockIconImage", Image::class.java).invoke(instance, ImageIcon(icon.file().absolutePath).image)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        Lwjgl3Application(Game(), Lwjgl3ApplicationConfiguration().apply {
            setTitle("KMine")
            setWindowSizeLimits(640, 580, -1, -1)
            setHdpiMode(HdpiMode.Pixels)
            useVsync(true)
            setForegroundFPS(0)
            setInitialBackgroundColor(Color(60 / 255f, 174 / 255f, 243 / 255f, 1f))
            setWindowIcon("icon128.png","icon64.png", "icon32.png", "icon16.png")
        }).apply {
            setDockIconForMac()
        }
    }

}
