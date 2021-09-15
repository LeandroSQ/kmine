package quevedo.soares.leandro.kmine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.CubeTexture
import quevedo.soares.leandro.kmine.cube.type.BedrockCube
import quevedo.soares.leandro.kmine.cube.type.DirtCube
import quevedo.soares.leandro.kmine.cube.type.GrassCube
import quevedo.soares.leandro.kmine.cube.type.StoneCube

class CubeChunk {

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
		-0.5f, -0.5f,  0.5f,
		0.5f,  -0.5f,  0.5f,
		0.5f,   0.5f,  0.5f,
		-0.5f,  0.5f,  0.5f
	)

	private val backFace = floatArrayOf(
		// x   y    z
		0.5f, -0.5f, -0.5f,
		-0.5f, -0.5f, -0.5f,
		-0.5f,  0.5f, -0.5f,
		0.5f,  0.5f, -0.5f
	)

	private val topFace = floatArrayOf(
		-0.5f,  0.5f, -0.5f,
		-0.5f,  0.5f,  0.5f,
		0.5f,  0.5f,  0.5f,
		0.5f,  0.5f, -0.5f
	)

	private val bottomFace = floatArrayOf(
		-0.5f, -0.5f, -0.5f,
		0.5f,  -0.5f, -0.5f,
		0.5f,  -0.5f,  0.5f,
		-0.5f, -0.5f,  0.5f
	)

	private val rightFace = floatArrayOf(
		0.5f, -0.5f,  0.5f,
		0.5f, -0.5f, -0.5f,
		0.5f,  0.5f, -0.5f,
		0.5f,  0.5f,  0.5f
	)

	private val leftFace = floatArrayOf(
		-0.5f, -0.5f, -0.5f,
		-0.5f, -0.5f,  0.5f,
		-0.5f,  0.5f,  0.5f,
		-0.5f,  0.5f, -0.5f
	)
	// endregion

	private lateinit var texture: Texture
	private lateinit var textureAtlas: TextureAtlas

	private lateinit var model: Model
	private lateinit var modelInstance: ModelInstance
	private var cubes: ArrayList<ArrayList<ArrayList<Cube?>>> = arrayListOf()
	val xCount get() = this.cubes.size
	val yCount get() = this.cubes.first().size
	val zCount get() = this.cubes.first().first().size

	fun generate() {
		// 1 - Bedrock
		// 2 - Stone
		// 3 - Grass

		val chunkSize = 16

		val xBuffer = arrayListOf<ArrayList<ArrayList<Cube?>>>()
		for (x in 0 until chunkSize) {
			val yBuffer = arrayListOf<ArrayList<Cube?>>()
			for (y in 0 until chunkSize + 1) {
				val zBuffer = arrayListOf<Cube?>()
				for (z in 0 until chunkSize) {
					val position = Vector3(x.toFloat(), y.toFloat(), z.toFloat())
					when {
						y <= 0 -> {
							zBuffer.add(BedrockCube(position))
						}

						y <= 2 -> {
							zBuffer.add(StoneCube(position))
						}

						y <= chunkSize - 1 -> {
							zBuffer.add(DirtCube(position))
						}

						else -> {
							if (Math.random() <= 0.5) zBuffer.add(GrassCube(position))
							else zBuffer.add(null)
						}
					}
				}
				yBuffer.add(zBuffer)
			}
			xBuffer.add(yBuffer)
		}

		this.cubes = xBuffer

		this.loadTextures()
		this.generateMesh()
	}

	fun getCubeAt(x: Int, y: Int, z: Int): Cube? {
		if (x >= this.xCount || y >= this.yCount || z >= this.zCount || x < 0 || y < 0 || z < 0 ) return null
		return cubes[x][y][z]
	}

	inline fun isCubeEmpty(x: Int, y: Int, z: Int)= this.getCubeAt(x, y, z) == null

	private fun MeshPartBuilder.addQuad(vertices: FloatArray, normals: FloatArray, textureRegion: CubeTexture) {
		this.setUVRange(textureAtlas.findRegion(textureRegion.regionName))
		this.rect(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5], vertices[6], vertices[7], vertices[8], vertices[9], vertices[10], vertices[11], normals[0], normals[1], normals[2])
	}

	fun loadTextures() {
		this.textureAtlas = TextureAtlas(Gdx.files.local("cubes.atlas"))
		this.texture = textureAtlas.textures.first()
	}

	fun FloatArray.multiply(other: IntArray): FloatArray {
		this.clone().apply {
			for (i in 0 until this.size) {
				val j = i % other.size
				this[i] = this[i] + other[j]
			}

			return this
		}
	}

	fun generateMesh() {
		val material = Material(TextureAttribute.createDiffuse(this.texture))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()

		this.model = ModelBuilder().run {
			begin()

			part("mesh", GL20.GL_TRIANGLES, attributes, material).apply {
				for (x in 0 until cubes.size) {
					for (y in 0 until cubes[x].size) {
						for (z in 0 until cubes[x][y].size) {
							val cube = cubes[x][y][z] ?: continue

							// Top
							if (isCubeEmpty(x, y + 1, z)) {
								addQuad(topFace.multiply(intArrayOf(x, y, z)), topNormal, cube.textureMap.top)
							}

							// Bottom
							if (isCubeEmpty(x, y - 1, z)) {
								addQuad(bottomFace.multiply(intArrayOf(x, y, z)), bottomNormal, cube.textureMap.bottom)
							}

							// Left
							if (isCubeEmpty(x - 1, y, z)) {
								addQuad(leftFace.multiply(intArrayOf(x, y, z)), leftNormal, cube.textureMap.left)
							}

							// Right
							if (isCubeEmpty(x + 1, y, z)) {
								addQuad(rightFace.multiply(intArrayOf(x, y, z)), rightNormal, cube.textureMap.right)
							}

							// Front
							if (isCubeEmpty(x, y, z + 1)) {
								addQuad(frontFace.multiply(intArrayOf(x, y, z)), frontNormal, cube.textureMap.front)
							}

							// Back
							if (isCubeEmpty(x, y, z - 1)) {
								addQuad(backFace.multiply(intArrayOf(x, y, z)), backNormal, cube.textureMap.back)
							}
						}
					}
				}
			}

			end()
		}
		this.modelInstance = ModelInstance(this.model)
	}

	/*private fun MeshPartBuilder.addQuad(vertices: FloatArray, normals: FloatArray, textureAtlas: TextureAtlas, textureRegion: CubeTexture) {
		this.setUVRange(textureAtlas.findRegion(textureRegion.regionName))
		this.rect(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5], vertices[6], vertices[7], vertices[8], vertices[9], vertices[10], vertices[11], normals[0], normals[1], normals[2])
	}

	private fun createMesh() {
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
	}*/

	private fun iterateTroughCubes(callback: (Cube) -> Unit) {
		for (x in this.cubes) {
			for (y in x) {
				for (cube in y) {
					cube?.let(callback)
				}
			}
		}
	}

	fun render(modelBatch: ModelBatch, environment: Environment) {
		modelBatch.render(this.modelInstance, environment)
		/*iterateTroughCubes {
			modelBatch.render(it.modelInstance, environment)
		}*/
	}

	fun dispose() {
		this.texture.dispose()
		this.model.dispose()
	}

}

class TerrainBuilder {



}