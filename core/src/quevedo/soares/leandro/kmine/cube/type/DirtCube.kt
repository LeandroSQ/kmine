package quevedo.soares.leandro.kmine.cube.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.cube.CubeTexture

class DirtCube : Cube {

	override var textureMap = CubeFaceTextureMap(
		top = CubeTexture.DIRT,
		bottom = CubeTexture.DIRT,
		front = CubeTexture.DIRT,
		back = CubeTexture.DIRT,
		left = CubeTexture.DIRT,
		right = CubeTexture.DIRT
	)

	constructor() : super() {

	}

	constructor(position: Vector3) : super(position) {

	}
}