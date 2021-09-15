package quevedo.soares.leandro.kmine.cube

import com.badlogic.gdx.math.Vector3

abstract class Cube {

	open var position: Vector3 = Vector3.Zero
	open lateinit var textureMap: CubeFaceTextureMap
	open var isTranslucent: Boolean = false

	constructor() { }
	constructor(position: Vector3) {
		this.position = position
	}

}