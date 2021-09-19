package quevedo.soares.leandro.kmine.core.terrain.biome

import quevedo.soares.leandro.kmine.core.terrain.BiomeInfluence
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.terrain.Terrain
import quevedo.soares.leandro.kmine.core.terrain.type.BedrockCube
import quevedo.soares.leandro.kmine.core.terrain.type.SandCube

class DuneBiome(generator: Terrain) : Biome(generator) {

	override fun getHeightAt(x: Float, z: Float): Float {
		val w = this.generator.width.toFloat()
		val h = this.generator.height.toFloat()
		val d = this.generator.depth.toFloat()

		// Calculate the noise
		return this.simplexNoise.noise2D(
			x = x / w,
			z = z / d,
			min = h / 4f,
			max = h / 2.7f
		)
	}

	// 25% of chance to get a higher bedrock layer
	private fun getBedrockLevel() = if (Math.random() <= 0.25) 1 else 0

	override fun fill(x: Float, z: Float, nearby: List<BiomeInfluence>): ArrayList<Cube?> {
		val height = this.interpolateHeightAt(x, z, nearby).toInt()

		// Generate an empty cube strip
		val strip = arrayListOf<Cube?>()

		// Generate the bedrock layer
		val bedrockLevel = this.getBedrockLevel()
		if (bedrockLevel >= 1) strip.add(Chunk.EMPTY)
		strip.add(BedrockCube())


		// Fill with middle cubes
		for (y in bedrockLevel + 1 until height) {
			strip.add(SandCube())
		}

		return strip
	}

}