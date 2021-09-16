package quevedo.soares.leandro.kmine.desktop

import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.graphics.Color
import quevedo.soares.leandro.kmine.Game

object DesktopLauncher {

    @JvmStatic
    fun main(arg: Array<String>) {
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "false");

        LwjglApplication(Game(), LwjglApplicationConfiguration().apply {
            title = "KMine"
            x = -1
            y = -1
            initialBackgroundColor = Color(60 / 255f, 174 / 255f, 243 / 255f, 1f)
            width = 640
            height = 480
//            useHDPI = true
            samples = 3
            vSyncEnabled = false
            foregroundFPS = 0
        })
    }

}