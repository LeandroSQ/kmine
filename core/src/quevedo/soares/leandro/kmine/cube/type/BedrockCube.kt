package quevedo.soares.leandro.kmine.cube.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.cube.CubeTexture

class BedrockCube : Cube {

	override var textureMap = CubeFaceTextureMap(
		top = CubeTexture.BEDROCK,
		bottom = CubeTexture.BEDROCK,
		front = CubeTexture.BEDROCK,
		back = CubeTexture.BEDROCK,
		left = CubeTexture.BEDROCK,
		right = CubeTexture.BEDROCK
	)

	constructor() : super() {

	}

	constructor(position: Vector3) : super(position) {

	}

}