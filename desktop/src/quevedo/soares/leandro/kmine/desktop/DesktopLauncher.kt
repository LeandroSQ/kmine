package quevedo.soares.leandro.kmine.desktop

import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import quevedo.soares.leandro.kmine.Game

object DesktopLauncher {

    @JvmStatic
    fun main(arg: Array<String>) {
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");

        LwjglApplication(Game(), LwjglApplicationConfiguration().apply {
            title = "KMine"
            x = -1
            y = -1
            width = 800
            height = 400
            useHDPI = true
            samples = 1
            vSyncEnabled = true
            foregroundFPS = 0
        })
    }

}