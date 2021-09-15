package quevedo.soares.leandro.kmine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.CubeTexture

class Chunk {

	private lateinit var model: Model
	private lateinit var modelInstance: ModelInstance
	var cubes: ArrayList<ArrayList<ArrayList<Cube?>>> = arrayListOf()
	val xCount get() = this.cubes.size
	val yCount get() = this.cubes.first().size
	val zCount get() = this.cubes.first().first().size
	var origin = Vector3.Zero

	fun fillWith(size: Int, altitude: Int, cube: Cube? = null) {
		val xBuffer = arrayListOf<ArrayList<ArrayList<Cube?>>>()
		for (x in 0 until size) {
			val yBuffer = arrayListOf<ArrayList<Cube?>>()

			for (y in 0 until altitude) {
				val zBuffer = arrayListOf<Cube?>()

				for (z in 0 until size) {
					zBuffer.add(cube)
				}

				yBuffer.add(zBuffer)
			}

			xBuffer.add(yBuffer)
		}

		this.cubes = xBuffer
	}

	fun getCubeAt(x: Int, y: Int, z: Int): Cube? {
		if (x >= this.xCount || y >= this.yCount || z >= this.zCount || x < 0 || y < 0 || z < 0 ) return null
		return cubes[x][y][z]
	}

	fun setCubeAt(x: Int, y: Int, z: Int, cube: Cube?) {
		cube?.position = Vector3(x.toFloat(), y.toFloat(), z.toFloat())
		this.cubes[x][y][z] = cube
	}

	private inline fun isCubeEmpty(x: Int, y: Int, z: Int) = this.getCubeAt(x, y, z) == null

	private fun MeshPartBuilder.addQuad(vertices: FloatArray, normals: FloatArray, origin: FloatArray, textureAtlas: TextureAtlas, textureRegion: CubeTexture) {
		val v = vertices.add(origin)
		this.setUVRange(textureAtlas.findRegion(textureRegion.regionName))
		this.rect(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], normals[0], normals[1], normals[2])
	}

	fun FloatArray.add(other: FloatArray): FloatArray {
		this.clone().apply {
			for (i in 0 until this.size) {
				val j = i % other.size
				this[i] = this[i] + other[j]
			}

			return this
		}
	}

	fun generateMesh() {
		val material = Material(TextureAttribute.createDiffuse(texture))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()

		this.model = ModelBuilder().run {
			begin()

			part("mesh", GL20.GL_TRIANGLES, attributes, material).apply {
				for (x in 0 until cubes.size) {
					for (y in 0 until cubes[x].size) {
						for (z in 0 until cubes[x][y].size) {
							// Ignore empty cubes
							val cube = cubes[x][y][z] ?: continue

							// Define the cube position
							val position = floatArrayOf(x + origin.x, y + origin.y, z + origin.z)

							// Top
							if (isCubeEmpty(x, y + 1, z)) addQuad(topFace, topNormal, position, atlas, cube.textureMap.top)
							// Bottom
							if (isCubeEmpty(x, y - 1, z)) addQuad(bottomFace, bottomNormal, position, atlas, cube.textureMap.bottom)
							// Left
							if (isCubeEmpty(x - 1, y, z)) addQuad(leftFace, leftNormal, position, atlas, cube.textureMap.left)
							// Right
							if (isCubeEmpty(x + 1, y, z)) addQuad(rightFace, rightNormal, position, atlas, cube.textureMap.right)
							// Front
							if (isCubeEmpty(x, y, z + 1)) addQuad(frontFace, frontNormal, position, atlas, cube.textureMap.front)
							// Back
							if (isCubeEmpty(x, y, z - 1)) addQuad(backFace, backNormal, position, atlas, cube.textureMap.back)

						}
					}
				}
			}

			end()
		}
		this.modelInstance = ModelInstance(this.model)
	}

	fun render(modelBatch: ModelBatch, environment: Environment) {
		modelBatch.render(this.modelInstance, environment)
	}

	fun dispose() {
		this.model.dispose()
	}

	companion object {

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
		private lateinit var atlas: TextureAtlas

		init {
			this.loadTextures()
		}

		private fun loadTextures() {
			this.atlas = TextureAtlas(Gdx.files.local("cubes.atlas"))
			this.texture = atlas.textures.first()
		}

		fun disposeTextures() {
			this.texture.dispose()
		}
	}

}