package quevedo.soares.leandro.kmine.core.terrain.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.models.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.core.enums.CubeTexture

class GlassCube : Cube {

	override var textureMap = CubeFaceTextureMap(
		singleTexture = CubeTexture.GLASS,
	)

	override var isTranslucent = true

	constructor() : super() {

	}

	constructor(position: Vector3) : super(position) {

	}
}