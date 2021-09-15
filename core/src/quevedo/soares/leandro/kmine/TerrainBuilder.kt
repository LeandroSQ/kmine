package quevedo.soares.leandro.kmine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelCache
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3

/*
data class CubeSurrounding (
	val top: Boolean,
	val bottom: Boolean,
	val left: Boolean,
	val right: Boolean,
	val front: Boolean,
	val back: Boolean
)

data class Cube(
	var position: Vector3,
	var color: Color
)

data class Chunk(var cubes: ArrayList<ArrayList<ArrayList<Cube?>>>) {

	val zCount get() = this.cubes.size
	val xCount get() = this.cubes.first().size
	val yCount get() = this.cubes.first().first().size

	fun isEmpty(x: Int, y: Int, z: Int): Boolean {
		if (x > this.xCount || y > this.yCount || z > this.zCount) return true

		return this.cubes[z][y][x] == null
	}

	fun getSurroundings(x: Int, y: Int, z: Int) = CubeSurrounding(
		this.isEmpty(x, y + 1, z),
		this.isEmpty(x, y - 1, z),
		this.isEmpty(x - 1, y, z),
		this.isEmpty(x + 1, y, z),
		this.isEmpty(x, y, z + 1),
		this.isEmpty(x, y, z - 1)
	)

}

class TerrainBuilder {
	
	// region Faces
	private val frontFace = floatArrayOf(
		-1.0f, -1.0f,  1.0f,
		1.0f,  -1.0f,  1.0f,
		1.0f,   1.0f,  1.0f,
		-1.0f,  1.0f,  1.0f
	)
	
	private val topFace = floatArrayOf(
		-1.0f,  1.0f, -1.0f,
		-1.0f,  1.0f,  1.0f,
		1.0f,   1.0f,  1.0f,
		1.0f,   1.0f, -1.0f
	)
	
	private val bottomFace = floatArrayOf(
		-1.0f, -1.0f, -1.0f,
		1.0f,  -1.0f, -1.0f,
		1.0f,  -1.0f,  1.0f,
		-1.0f, -1.0f,  1.0f
	)
	
	private val rightFace = floatArrayOf(
		1.0f, -1.0f, -1.0f,
		1.0f,  1.0f, -1.0f,
		1.0f,  1.0f,  1.0f,
		1.0f, -1.0f,  1.0f
	)
	
	private val leftFace = floatArrayOf(
		-1.0f, -1.0f, -1.0f,
		-1.0f, -1.0f,  1.0f,
		-1.0f,  1.0f,  1.0f,
		-1.0f,  1.0f, -1.0f
	)
	// endregion
	
	// region Indices
	private val frontFaceIndices = shortArrayOf(
		0,  1,  2,     0,  2,  3    // front
	)

	private val backFaceIndices = shortArrayOf(
		4,  5,  6,      4,  6,  7    // back
	)

	private val topFaceIndices = shortArrayOf(
		8,  9,  10,     8,  10, 11   // top
	)

	private val bottomFaceIndices = shortArrayOf(
		12, 13, 14,     12, 14, 15   // bottom
	)

	private val rightFaceIndices = shortArrayOf(
		16, 17, 18,     16, 18, 19   // right
	)

	private val leftFaceIndices = shortArrayOf(
		20, 21, 22,     20, 22, 23    // left
	)
	// endregion

	var chunks = arrayListOf<Chunk>()

	fun generateChunk() {
		val rows = 10
		val cols = 10
		val height = 5

		val rowBuffer = arrayListOf<ArrayList<ArrayList<Cube?>>>()
		for (row in 0..rows) {
			val colBuffer = arrayListOf<ArrayList<Cube?>>()
			for (col in 0..cols) {
				val altitudeBuffer = arrayListOf<Cube?>()
				for (altitude in 0..height) {
					if (altitude > 2 && Math.random() < 0.5f) {
						altitudeBuffer.add(
							Cube(
								position = Vector3(col.toFloat(), altitude.toFloat(), row.toFloat()),
								color = Color.CORAL
							)
						)
					} else {
						altitudeBuffer.add(null)
					}
				}
				colBuffer.add(altitudeBuffer)
			}
			rowBuffer.add(colBuffer)
		}

		chunks = arrayListOf()
		chunks.add(Chunk(rowBuffer))
	}

	fun loadTexture(id: String) = Texture(Gdx.files.local(id))

	fun generateMesh() {
		val attributes = VertexAttributes.Usage.Position or VertexAttributes.Usage.TextureCoordinates or VertexAttributes.Usage.Normal

		for (chunk in this.chunks) {
			for (z in 0..chunk.zCount) {
				for (y in 0..chunk.yCount) {
					for (x in 0..chunk.xCount) {
						val surroundings = chunk.getSurroundings(x, y, z)

						if (surroundings.top) {

						}

					}
				}
			}
		}
	}

}*/
