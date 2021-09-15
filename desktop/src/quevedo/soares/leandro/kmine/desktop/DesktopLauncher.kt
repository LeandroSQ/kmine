package quevedo.soares.leandro.kmine.desktop

import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import quevedo.soares.leandro.kmine.Game

object DesktopLauncher {

    @JvmStatic
    fun main(arg: Array<String>) {
        LwjglApplication(Game(), LwjglApplicationConfiguration().apply {
            title = "KMine"
            width = 800
            height = 400
            samples = 32
            resizable = false
            vSyncEnabled = true
            foregroundFPS = 0
        })
    }

}