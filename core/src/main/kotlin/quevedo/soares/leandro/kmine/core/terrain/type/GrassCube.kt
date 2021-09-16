package quevedo.soares.leandro.kmine.core.terrain.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.models.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.core.enums.CubeTexture

class GrassCube : Cube {

	override var textureMap = CubeFaceTextureMap(
		top = CubeTexture.GRASS_TOP,
		bottom = CubeTexture.DIRT,
		front = CubeTexture.GRASS_SIDES,
		back = CubeTexture.GRASS_SIDES,
		left = CubeTexture.GRASS_SIDES,
		right = CubeTexture.GRASS_SIDES
	)

	constructor() : super() {

	}

	constructor(position: Vector3) : super(position) {

	}

}