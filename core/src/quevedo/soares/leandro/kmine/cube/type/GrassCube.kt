package quevedo.soares.leandro.kmine.cube.type

import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.cube.CubeTexture

class GrassCube : Cube() {

	override val textureMap: CubeFaceTextureMap = CubeFaceTextureMap(
		top = CubeTexture.GRASS_TOP,
		bottom = CubeTexture.DIRT,
		front = CubeTexture.GRASS_SIDES,
		back = CubeTexture.GRASS_SIDES,
		left = CubeTexture.GRASS_SIDES,
		right = CubeTexture.GRASS_SIDES
	)

}