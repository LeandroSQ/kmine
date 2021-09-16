package quevedo.soares.leandro.kmine.core.terrain.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.models.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.core.enums.CubeTexture

class StoneCube : Cube {

	override var textureMap = CubeFaceTextureMap(
		singleTexture = CubeTexture.STONE
	)

	constructor() : super() {

	}

	constructor(position: Vector3) : super(position) {

	}

}