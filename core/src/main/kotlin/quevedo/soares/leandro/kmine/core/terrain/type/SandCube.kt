package quevedo.soares.leandro.kmine.core.terrain.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.models.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.core.enums.CubeTexture
import quevedo.soares.leandro.kmine.core.terrain.FallingCube

class SandCube : FallingCube {

	override var textureMap = CubeFaceTextureMap(
		singleTexture = CubeTexture.SAND
	)

	constructor() : super() {

	}

	constructor(position: Vector3) : super(position) {

	}

}