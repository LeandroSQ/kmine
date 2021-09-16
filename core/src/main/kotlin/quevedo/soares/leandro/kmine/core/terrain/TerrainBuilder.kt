package quevedo.soares.leandro.kmine.core.terrain

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.terrain.type.BedrockCube
import quevedo.soares.leandro.kmine.core.terrain.type.DirtCube
import quevedo.soares.leandro.kmine.core.utils.OpenSimplexNoise

object TerrainBuilder {

	private var simplexNoise = OpenSimplexNoise(System.currentTimeMillis())

	private fun map(value: Float, istart: Float, istop: Float, ostart: Float, ostop: Float): Float {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart))
	}

	private fun noise(x: Double, y: Double, z: Double? = null, min: Float = 0f, max: Float = 1f): Double {
		val noise = if (z != null) simplexNoise.noise3_Classic(x, y, z)
		else simplexNoise.noise2(x, y)

		return map(noise.toFloat(), -1f, 1f, min, max).toDouble()
	}

	fun generateChunk(origin: Vector3): Chunk {
		// Initializes the chunk
		val chunk = Chunk()
		chunk.origin = origin

		// Fills the chunk with air
		val chunkSize = 16
		val chunkAltitude = 64
		chunk.fillWith(chunkSize, chunkAltitude, null)

		// Generate the terrain
		for (x in 0 until chunk.cubes.size) {
			for (y in 0 until chunk.cubes[x].size) {
				for (z in 0 until chunk.cubes[x][y].size) {
					if (y <= 0) {
						chunk.setCubeAt(x, y, z, BedrockCube())
						continue
					}

					val noise = noise(
						x = (x + origin.x) / chunkSize.toDouble(),
						y = (y + origin.y) / chunkAltitude.toDouble(),
						z = (z + origin.z) / chunkSize.toDouble(),
						min = 0f,
						max = 1f
					)

					if (noise > 0.5) {
						chunk.setCubeAt(x, y, z, DirtCube())
					}

					// println("$x, $y, $z -> $noise")
					/*if (y + 1 < noise) {
						chunk.setCubeAt(x, y, z, DirtCube())
					} else if (y < noise) {
						chunk.setCubeAt(x, y, z, GrassCube())
					}*/
				}
			}
		}

		return chunk
	}

	fun generateWorld(chunkCount: Int): ArrayList<Chunk> {
		val chunks = arrayListOf<Chunk>()
		// Generates the chunks
		for(x in 0 until chunkCount) {
			for(z in 0 until chunkCount) {
				val chunk = generateChunk(origin = Vector3(x.toFloat() * 16, 0f, z.toFloat() * 16))
				chunks.add(chunk)
			}
		}

		return chunks
	}

}