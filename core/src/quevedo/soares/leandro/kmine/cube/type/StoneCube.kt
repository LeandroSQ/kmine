package quevedo.soares.leandro.kmine.cube.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.cube.CubeTexture

class StoneCube : Cube {

	override var textureMap = CubeFaceTextureMap(
		top = CubeTexture.STONE,
		bottom = CubeTexture.STONE,
		front = CubeTexture.STONE,
		back = CubeTexture.STONE,
		left = CubeTexture.STONE,
		right = CubeTexture.STONE
	)

	constructor() : super() {

	}

	constructor(position: Vector3) : super(position) {

	}

}