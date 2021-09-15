package quevedo.soares.leandro.kmine.desktop

import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.graphics.Color
import quevedo.soares.leandro.kmine.Game

object DesktopLauncher {

    @JvmStatic
    fun main(arg: Array<String>) {
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");

        LwjglApplication(Game(), LwjglApplicationConfiguration().apply {
            title = "KMine"
            x = -1
            y = -1
            initialBackgroundColor = Color(60 / 255f, 174 / 255f, 243 / 255f, 1f)
            width = 800
            height = 400
            useHDPI = true
            samples = 1
            vSyncEnabled = true
            foregroundFPS = 0
        })
    }

}