package quevedo.soares.leandro.kmine.cube.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.cube.CubeTexture

class DirtCube : Cube {

	constructor(position: Vector3) : super(position, CubeFaceTextureMap(
		top = CubeTexture.DIRT,
		bottom = CubeTexture.DIRT,
		front = CubeTexture.DIRT,
		back = CubeTexture.DIRT,
		left = CubeTexture.DIRT,
		right = CubeTexture.DIRT
	))

}