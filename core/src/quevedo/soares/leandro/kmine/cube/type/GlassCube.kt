package quevedo.soares.leandro.kmine.cube.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.cube.CubeTexture

class GlassCube : Cube {

	override var textureMap = CubeFaceTextureMap(
		top = CubeTexture.GLASS,
		bottom = CubeTexture.GLASS,
		front = CubeTexture.GLASS,
		back = CubeTexture.GLASS,
		left = CubeTexture.GLASS,
		right = CubeTexture.GLASS
	)

	override var isTranslucent = true

	constructor() : super() {

	}

	constructor(position: Vector3) : super(position) {

	}
}