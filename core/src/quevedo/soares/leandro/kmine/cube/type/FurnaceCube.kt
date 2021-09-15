package quevedo.soares.leandro.kmine.cube.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.cube.CubeTexture

class FurnaceCube : Cube {

	override var textureMap = CubeFaceTextureMap(
		top = CubeTexture.FURNACE_TOP,
		bottom = CubeTexture.FURNACE_TOP,
		front = CubeTexture.FURNACE_FRONT,
		back = CubeTexture.FURNACE_SIDES,
		left = CubeTexture.FURNACE_SIDES,
		right = CubeTexture.FURNACE_SIDES
	)

	constructor() : super() {

	}

	constructor(position: Vector3) : super(position) {

	}

}