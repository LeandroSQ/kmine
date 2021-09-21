package quevedo.soares.leandro.kmine.core.models

import quevedo.soares.leandro.kmine.core.terrain.biome.Biome


data class BiomeInfluence(
	val biome: Biome,
	val influence: Float
) {

	override fun toString() = "${biome.name} - ${influence}"

}