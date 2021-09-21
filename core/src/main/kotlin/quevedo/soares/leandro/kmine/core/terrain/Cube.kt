package quevedo.soares.leandro.kmine.core.terrain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.models.CubeFaceTextureMap

abstract class Cube {

	open val size = 0.5f
	open var position: Vector3 = Vector3(0f, 0f, 0f)
	var chunk: Chunk? = null

	open lateinit var textureMap: CubeFaceTextureMap

	open var isTranslucent: Boolean = false

	constructor()
	constructor(position: Vector3) {
		this.position = position
	}
	constructor(position: Vector3, chunk: Chunk) {
		this.position = position
		this.chunk = chunk
	}

	open val frontNormal get() = Cube.frontNormal
	open val frontFace get() = Cube.frontFace

	open val backNormal get() = Cube.backNormal
	open val backFace get() = Cube.backFace

	open val topNormal get() = Cube.topNormal
	open val topFace get() = Cube.topFace

	open val leftNormal get() = Cube.leftNormal
	open val leftFace get() = Cube.leftFace

	open val rightNormal get() = Cube.rightNormal
	open val rightFace get() = Cube.rightFace

	open val bottomNormal get() = Cube.bottomNormal
	open val bottomFace get() = Cube.bottomFace

	companion object {

		// region Normals
		val frontNormal = floatArrayOf(0f, 0f, 1f)

		val backNormal = floatArrayOf(0f, 0f, -1f)

		val topNormal = floatArrayOf(0f, 1f, 0f)

		val bottomNormal = floatArrayOf(0f, -1f, 0f)

		val leftNormal = floatArrayOf(-1f, 0f, 0f)

		val rightNormal = floatArrayOf(1f, 0f, 0f)
		// endregion

		// region Faces
		val frontFace = floatArrayOf(
			-0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			0.5f, 0.5f, 0.5f,
			-0.5f, 0.5f, 0.5f
		)

		val backFace = floatArrayOf(
			// x   y    z
			0.5f, -0.5f, -0.5f,
			-0.5f, -0.5f, -0.5f,
			-0.5f, 0.5f, -0.5f,
			0.5f, 0.5f, -0.5f
		)

		val topFace = floatArrayOf(
			-0.5f, 0.5f, -0.5f,
			-0.5f, 0.5f, 0.5f,
			0.5f, 0.5f, 0.5f,
			0.5f, 0.5f, -0.5f
		)

		val bottomFace = floatArrayOf(
			-0.5f, -0.5f, -0.5f,
			0.5f, -0.5f, -0.5f,
			0.5f, -0.5f, 0.5f,
			-0.5f, -0.5f, 0.5f
		)

		val rightFace = floatArrayOf(
			0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, -0.5f,
			0.5f, 0.5f, -0.5f,
			0.5f, 0.5f, 0.5f
		)

		val leftFace = floatArrayOf(
			-0.5f, -0.5f, -0.5f,
			-0.5f, -0.5f, 0.5f,
			-0.5f, 0.5f, 0.5f,
			-0.5f, 0.5f, -0.5f
		)
		// endregion

		// region Textures
		val atlas: TextureAtlas = TextureAtlas(Gdx.files.local("cubes.atlas"))
		val texture: Texture = atlas.textures.first()

		fun disposeTextures() {
			texture.dispose()
		}
		// endregion
	}

}