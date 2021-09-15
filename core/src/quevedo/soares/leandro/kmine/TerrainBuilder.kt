package quevedo.soares.leandro.kmine

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.type.BedrockCube
import quevedo.soares.leandro.kmine.cube.type.GrassCube
import quevedo.soares.leandro.kmine.cube.type.StoneCube

class CubeChunk {

	private var cubes: ArrayList<ArrayList<ArrayList<Cube>>> = arrayListOf()

	fun generate() {
		// 1 - Bedrock
		// 2 - Stone
		// 3 - Grass

		val chunkSize = 4

		val xBuffer = arrayListOf<ArrayList<ArrayList<Cube>>>()
		for (x in 0..chunkSize) {
			val yBuffer = arrayListOf<ArrayList<Cube>>()
			for (y in 0..chunkSize) {
				val zBuffer = arrayListOf<Cube>()
				for (z in 0..chunkSize) {
					val position = Vector3(x.toFloat() * 2f, y.toFloat() * 2f, z.toFloat() * 2f)
					when {
						y <= 0 -> {
							zBuffer.add(BedrockCube(position))
						}

						y <= 2 -> {
							zBuffer.add(StoneCube(position))
						}

						else -> {
							zBuffer.add(GrassCube(position))
						}
					}
				}
				yBuffer.add(zBuffer)
			}
			xBuffer.add(yBuffer)
		}

		this.cubes = xBuffer

	}

	private fun iterateTroughCubes(callback: (Cube) -> Unit) {
		for (x in this.cubes) {
			for (y in x) {
				for (cube in y) {
					callback.invoke(cube)
				}
			}
		}
	}

	fun render(modelBatch: ModelBatch, environment: Environment) {
		iterateTroughCubes {
			modelBatch.render(it.modelInstance, environment)
		}
	}

	fun dispose() {
		iterateTroughCubes {
			it.dispose()
		}
	}

}

class TerrainBuilder {



}