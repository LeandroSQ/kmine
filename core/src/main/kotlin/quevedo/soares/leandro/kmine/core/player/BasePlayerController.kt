package quevedo.soares.leandro.kmine.core.player

abstract class BasePlayerController {

	open fun create() {	}

	open fun activate() { }

	abstract fun update()

	open fun dispose() { }

}