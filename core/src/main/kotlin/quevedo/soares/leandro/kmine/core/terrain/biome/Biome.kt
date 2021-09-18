package quevedo.soares.leandro.kmine.core.terrain.biome

import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.TerrainGenerator

abstract class Biome() {

	abstract fun generate(terrainGenerator: TerrainGenerator, chunk: Chunk)

	abstract fun dispose()

}