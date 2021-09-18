package quevedo.soares.leandro.kmine.core.terrain.biome

import ktx.math.minus
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.TerrainGenerator
import quevedo.soares.leandro.kmine.core.terrain.type.BedrockCube
import quevedo.soares.leandro.kmine.core.terrain.type.DirtCube
import quevedo.soares.leandro.kmine.core.terrain.type.GrassCube
import quevedo.soares.leandro.kmine.core.utils.vec3

class DefaultBiome : Biome() {

	override fun generate(terrainGenerator: TerrainGenerator, chunk: Chunk) {
		chunk.cubes.forEach { x, y, z, cube ->
			// Ignore already defined cubes
			if (cube != Chunk.EMPTY) return@forEach

			val pos = vec3(x, y, z)

			// Bedrock layer
			if (y <= 0) {
				chunk.set(pos, BedrockCube())
			} else if (y <= 1 && Math.random() < 0.25f) {// Randomly places bedrock above layer 0
				// Removes the cube bellow
				chunk.set(pos - vec3(0, 1, 0), Chunk.EMPTY)
				chunk.set(pos, BedrockCube())
			} else {
				// Calculate the noise
				val noise = terrainGenerator.openSimplexNoise.noise2D(
					x = (x + chunk.position.x) / chunk.width,
					z = (z + chunk.position.z) / chunk.depth,
					min = chunk.height / 3f,
					max = chunk.height / 2f
				)

				if (y + 1 < noise) {
					chunk.set(pos, DirtCube())
				} else if (y < noise) {
					chunk.set(pos, GrassCube())
				}
			}

		}
	}

	override fun dispose() {

	}

}