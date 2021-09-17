package quevedo.soares.leandro.kmine.core.models

import quevedo.soares.leandro.kmine.core.enums.CubeTexture

data class CubeFaceTextureMap (
	val top: CubeTexture,
	val left: CubeTexture,
	val back: CubeTexture,
	val right: CubeTexture,
	val front: CubeTexture,
	val bottom: CubeTexture
) {
	constructor(singleTexture: CubeTexture) : this(singleTexture, singleTexture, singleTexture, singleTexture, singleTexture, singleTexture)
	constructor(top: CubeTexture, bottom: CubeTexture, sides: CubeTexture) : this(top, sides, sides, sides, sides, bottom)
}