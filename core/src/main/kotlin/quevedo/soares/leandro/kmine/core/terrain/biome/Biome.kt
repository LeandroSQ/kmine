package quevedo.soares.leandro.kmine.core.terrain.biome

import quevedo.soares.leandro.kmine.core.models.BiomeInfluence
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.terrain.Terrain

abstract class Biome(val generator: Terrain) {

	 val name: String = this::class.simpleName.toString()

	protected val simplexNoise get() = this.generator.openSimplexNoise

	protected fun average(vararg a: Float) = a.sum() / a.size

	private fun weightedAverage(values: List<Float>, weights: List<Float>): Float {
		val multipliedValues = values.mapIndexed() { index, value -> value * weights[index] }.sum()
		val weightSummation = weights.sum()
		return multipliedValues / weightSummation
	}

	protected fun interpolateHeightAt(x: Float, z: Float, nearby: List<BiomeInfluence>): Float {
		val values = arrayListOf<Float>().apply {
			add(getHeightAt(x, z))
			if (nearby.isNotEmpty()) addAll(nearby.map { it.biome.getHeightAt(x, z) })
		}

		val weights = arrayListOf<Float>().apply {
			add(1f)
			if (nearby.isNotEmpty()) addAll(nearby.map { it.influence })
		}

		return this.weightedAverage(values, weights)
	}

	abstract fun getHeightAt(x: Float, z: Float): Float

	abstract fun fill(x: Float, z: Float, nearby: List<BiomeInfluence>): ArrayList<Cube?>

	open fun dispose() { }

}