package quevedo.soares.leandro.kmine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.cube.type.BedrockCube
import quevedo.soares.leandro.kmine.cube.type.DirtCube
import quevedo.soares.leandro.kmine.cube.type.GrassCube
import quevedo.soares.leandro.kmine.cube.type.StoneCube

object TerrainBuilder {

	private var simplexNoise = OpenSimplexNoise(System.currentTimeMillis())

	private fun map(value: Float, istart: Float, istop: Float, ostart: Float, ostop: Float): Float {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart))
	}

	private fun noise(x: Double, y: Double, z: Double? = null, min: Float = 0f, max: Float = 1f): Double {
		val noise = if (z != null) simplexNoise.noise3_XZBeforeY(x, y, z.toDouble())
		else simplexNoise.noise2(x, y)

		return map(noise.toFloat(), -1f, 1f, min, max).toDouble()
	}


	fun generateChunk(origin: Vector3): Chunk {
		// Initializes the chunk
		val chunk = Chunk()
		chunk.origin = origin

		// Fills the chunk with air
		chunk.fillWith(16, 32, null)

		// Generate the terrain
		for (x in 0 until chunk.cubes.size) {
			for (y in 0 until chunk.cubes[x].size) {
				for (z in 0 until chunk.cubes[x][y].size) {
					if (y <= 0) {
						chunk.setCubeAt(x, y, z, BedrockCube())
						continue
					}

					val noise = noise((x + origin.x) / 32.0, (z + origin.z) / 32.0, min=8f, max=16f)

					// println("$x, $y, $z -> $noise")
					if (y + 1 < noise) {
						chunk.setCubeAt(x, y, z, DirtCube())
					} else if (y < noise) {
						chunk.setCubeAt(x, y, z, GrassCube())
					}
				}
			}
		}

		return chunk
	}

}