package quevedo.soares.leandro.kmine.core.terrain

import quevedo.soares.leandro.kmine.core.utils.vec3

class ChunkController {

	val chunkSize = 16
	val chunkAltitude = 32
	val chunks = arrayListOf<ArrayList<Chunk>>()

	fun getChunkAt(x: Int,  z: Int): Chunk? {
		val nx = x % chunkSize
		val nz = z % chunkSize

		return if (nx > this.chunks.size || nz > this.chunks[nx].size) null
		else this.chunks[nx][nz]
	}

	fun generateInitialChunks(amount: Int) {
		for (x in 0..amount) {
			for (y in 0..amount) {
				TerrainBuilder.generateChunk(vec3(x, y, 0))
			}
		}
	}

}