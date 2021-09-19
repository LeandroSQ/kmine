package quevedo.soares.leandro.kmine.core.terrain

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktx.math.div
import ktx.math.plus
import quevedo.soares.leandro.kmine.core.Game
import quevedo.soares.leandro.kmine.core.terrain.biome.Biome
import quevedo.soares.leandro.kmine.core.terrain.biome.DefaultBiome
import quevedo.soares.leandro.kmine.core.terrain.biome.DuneBiome
import quevedo.soares.leandro.kmine.core.utils.*
import java.util.*

private val SEED = Random().nextLong() + System.currentTimeMillis()

data class BiomeInfluence(
	val biome: Biome,
	val influence: Float
) {

	override fun toString() = "${biome.name} - ${influence}"

}

@Suppress("NOTHING_TO_INLINE")
class Terrain {

	val width = 16
	val height = 64
	val depth = 16
	val chunks = arrayListOf<Chunk>()

	internal lateinit var openSimplexNoise: OpenSimplexNoise

	private var biomes = arrayListOf<Biome>()

	fun create() {
		this.openSimplexNoise = OpenSimplexNoise(SEED)
		this.biomes.add(DefaultBiome(this))
		this.biomes.add(DuneBiome(this))
	}

	private inline fun getBiomeAt(position: Vector3) = this.getBiomeAt(position.xInt, position.zInt)
	private fun getBiomeAt(x: Int, z: Int): Biome {
		return this.biomes.random()

		// Calculates the divider, to scarse the x-z values
		// Therefore making the biome transitioning less often
		val divider = (width * height * depth) / 3f

		// Apply the noise function
		val index = (this.openSimplexNoise.noise2D(x / divider, z / divider, min = 0f, max = biomes.size.toFloat()))

		// Get the selected biome for the position
		return this.biomes[clamp(index.toInt(), 0, this.biomes.size - 1)]
	}

	private fun getAdjacentChunks(chunk: Chunk): ArrayList<Chunk> {
		// Normalizes the chunk position
		val relativePosition = chunk.position / vec3(width, height, depth)
		val adjacentChunks = arrayListOf<Chunk>()

		for (other in this.chunks) {
			if (other == chunk) continue

			// Normalizes the chunk position
			val otherRelativePosition = other.position / vec3(width, height, depth)

			// Calculate the distance of the chunk
			val distance = relativePosition.dist(otherRelativePosition)
			if (distance <= 1.5f) adjacentChunks.add(other)
		}

		return adjacentChunks
	}

	private fun calculateAdjacentBiomesInfluence (chunkPosition: Vector3, x: Int, z: Int, adjacentChunks: List<Chunk>): List<BiomeInfluence> {
		val cubePosition = vec2(chunkPosition.xInt, chunkPosition.zInt) + vec2(x, z)

		return adjacentChunks.map { adjacent ->
			val adjacentChunkPosition = vec2(adjacent.center.xInt, adjacent.center.zInt)
			val distance = adjacentChunkPosition.dist(cubePosition)

			BiomeInfluence(adjacent.biome, distance)
		}
	}

	fun generateChunk(position: Vector3) {
		// Fetch the biome for the chunk
		val biome = this.getBiomeAt(position)

		// Creates the chunk
		val chunk = Chunk(biome, position, width, height, depth).also {
			// Check for surrounding chunks
			it.neighbors = getAdjacentChunks(it)

			// Appends itself to its neighbors
			it.neighbors.forEach { other -> other.neighbors.add(it) }
		}

		runBlocking {
			// Generates the chunk cubes
			for (x in 0 until width) {
				for (z in 0 until depth) {
					launch {
						// Calculate the adjacent biomes influence onto the cube
						val influences = calculateAdjacentBiomesInfluence(position, x, z, chunk.neighbors)

						// Generate a cube strip from the biome
						val cubeStrip = biome.fill(position.x + x, position.z + z, influences)

						// Set the cube strip to the chunk
						cubeStrip.forEachIndexed { y, cube ->
							chunk.set(x, y, z, cube)
						}
					}
				}
			}
		}

		// Add it to the chunk list
		this.chunks.add(chunk)
	}

	fun generateBatch(count: Int, origin: Vector3 = Vector3.Zero) = runBlocking {
		/*generateChunk(origin + vec3(0 * width, 0, 0 * depth))
		generateChunk(origin + vec3(1 * width, 0, 0 * depth))*/

		for (x in 0 until count) {
			for (z in 0 until count) {
				//launch {
					generateChunk(origin + vec3(x * width, 0, z * depth))
				//}
			}
		}
	}

	fun dispose() {
		this.biomes.forEach { it.dispose() }
		this.biomes.clear()
	}

	fun render(modelBatch: ModelBatch, environment: Environment) {
		this.chunks.forEach {
			it.isVisible = Game.player.camera.frustum.boundsInFrustum(it.boundingBox)
			if (it.isVisible) it.render(modelBatch, environment)
		}
	}

}