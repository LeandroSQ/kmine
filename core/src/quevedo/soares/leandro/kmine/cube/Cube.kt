package quevedo.soares.leandro.kmine.cube

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3

abstract class Cube {

	// region Normals
	private val frontNormal = floatArrayOf(0f, 0f, 1f)

	private val backNormal = floatArrayOf(0f, 0f, -1f)

	private val topNormal = floatArrayOf(0f, 1f, 0f)

	private val bottomNormal = floatArrayOf(0f, -1f, 0f)

	private val leftNormal = floatArrayOf(-1f, 0f, 0f)

	private val rightNormal = floatArrayOf(1f, 0f, 0f)
	// endregion

	// region Faces
	private val frontFace = floatArrayOf(
		-1.0f, -1.0f,  1.0f,
		1.0f,  -1.0f,  1.0f,
		1.0f,   1.0f,  1.0f,
		-1.0f,  1.0f,  1.0f
	)

	private val backFace = floatArrayOf(
		// x   y    z
		 1.0f, -1.0f, -1.0f,
		-1.0f, -1.0f, -1.0f,
		-1.0f,  1.0f, -1.0f,
		 1.0f,  1.0f, -1.0f
	)

	private val topFace = floatArrayOf(
		-1.0f,  1.0f, -1.0f,
		-1.0f,  1.0f,  1.0f,
		 1.0f,  1.0f,  1.0f,
		 1.0f,  1.0f, -1.0f
	)

	private val bottomFace = floatArrayOf(
		-1.0f, -1.0f, -1.0f,
		 1.0f,  -1.0f, -1.0f,
		 1.0f,  -1.0f,  1.0f,
		-1.0f, -1.0f,  1.0f
	)

	private val rightFace = floatArrayOf(
 		1.0f, -1.0f,  1.0f,
		1.0f, -1.0f, -1.0f,
		1.0f,  1.0f, -1.0f,
		1.0f,  1.0f,  1.0f
	)

	private val leftFace = floatArrayOf(
		-1.0f, -1.0f, -1.0f,
		-1.0f, -1.0f,  1.0f,
		-1.0f,  1.0f,  1.0f,
		-1.0f,  1.0f, -1.0f
	)
	// endregion

	private var position: Vector3 = Vector3.Zero
		set(value) {
			field = value
			this.modelInstance.transform.setTranslation(value)
		}

	abstract val textureMap: CubeFaceTextureMap
	private lateinit var texture: Texture
	private lateinit var model: Model
	val modelInstance by lazy { ModelInstance(this.model) }

	fun dispose() {
		this.model.dispose()
		this.texture.dispose()
	}

	private fun MeshPartBuilder.addQuad(vertices: FloatArray, normals: FloatArray, textureAtlas: TextureAtlas, textureRegion: CubeTexture) {
		this.setUVRange(textureAtlas.findRegion(textureRegion.regionName))
		this.rect(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5], vertices[6], vertices[7], vertices[8], vertices[9], vertices[10], vertices[11], normals[0], normals[1], normals[2])
	}

	fun createMesh() {
		val textureAtlas = TextureAtlas(Gdx.files.local("cubes.atlas"))
		this.texture = textureAtlas.textures.first()

		val material = Material(TextureAttribute.createDiffuse(this.texture))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()

		this.model = ModelBuilder().run {
			begin()
			part("mesh", GL30.GL_TRIANGLES, attributes, material).apply {
				addQuad(topFace, topNormal, textureAtlas, textureMap.top)
				addQuad(bottomFace, bottomNormal, textureAtlas, textureMap.bottom)
				addQuad(frontFace, frontNormal, textureAtlas, textureMap.front)
				addQuad(backFace, backNormal, textureAtlas, textureMap.back)
				addQuad(leftFace, leftNormal, textureAtlas, textureMap.left)
				addQuad(rightFace, rightNormal, textureAtlas, textureMap.right)
			}
			end()
		}
	}

}