package quevedo.soares.leandro.kmine.core.terrain

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import kotlinx.coroutines.*
import ktx.async.KtxAsync
import ktx.async.newAsyncContext
import ktx.math.div
import ktx.math.plus
import ktx.math.vec3
import quevedo.soares.leandro.kmine.core.Game
import quevedo.soares.leandro.kmine.core.models.BiomeInfluence
import quevedo.soares.leandro.kmine.core.terrain.biome.Biome
import quevedo.soares.leandro.kmine.core.terrain.biome.DefaultBiome
import quevedo.soares.leandro.kmine.core.terrain.biome.DuneBiome
import quevedo.soares.leandro.kmine.core.utils.*
import java.util.*
import kotlin.system.measureTimeMillis

private val SEED = Random().nextLong() + System.currentTimeMillis()

@Suppress("NOTHING_TO_INLINE")
class Terrain {

	val width = 16
	val height = 64
	val depth = 16
	val chunks = arrayListOf<Chunk>()

	private val meshingContext = newAsyncContext(2, "TerrainMeshing-Thread")

	internal lateinit var openSimplexNoise: OpenSimplexNoise

	private var biomes = arrayListOf<Biome>()

	fun create() {
		this.openSimplexNoise = OpenSimplexNoise(SEED)
		this.biomes.add(DefaultBiome(this))
		this.biomes.add(DuneBiome(this))
	}

	private inline fun getBiomeAt(position: Vector3) = this.getBiomeAt(position.xInt, position.zInt)
	private fun getBiomeAt(x: Int, z: Int): Biome {
		return if (Math.floorMod(x / width + z / height, 2) == 0) biomes[0] else biomes[1]

		/*// Calculates the divider, to scarse the x-z values
		// Therefore making the biome transitioning less often
		val divider = (width * height * depth) / 3f

		// Apply the noise function
		val index = (this.openSimplexNoise.noise2D(x / divider, z / divider, min = 0f, max = biomes.size.toFloat()))

		// Get the selected biome for the position
		return this.biomes[clamp(index.toInt(), 0, this.biomes.size - 1)]*/
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

	private fun calculateAdjacentBiomesInfluence(chunkPosition: Vector3, x: Int, z: Int, adjacentChunks: List<Chunk>): List<BiomeInfluence> {
		val cubePosition = vec2(chunkPosition.xInt, chunkPosition.zInt) + vec2(x, z)

		return adjacentChunks.map { adjacent ->
			val adjacentChunkPosition = vec2(adjacent.center.xInt, adjacent.center.zInt)
			val distance = adjacentChunkPosition.dist(cubePosition)

			BiomeInfluence(adjacent.biome, distance)
		}
	}

	fun getChunkAt(x: Float, z: Float): Chunk? {
		return this.chunks.find {
			it.boundingBox.contains(vec3(x, 0f, z))
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

		// Generates the chunk cubes
		for (x in 0 until width) {
			for (z in 0 until depth) {
				// Calculate the adjacent biomes influence onto the cube
				val influences = calculateAdjacentBiomesInfluence(position, x, z, chunk.neighbors)

				// Generate a cube strip from the biome
				val cubeStrip = biome.fill(position.x + x, position.z + z, influences)

				// Set the cube strip to the chunk
				cubeStrip.forEachIndexed { y, cube ->
					cube?.position = vec3(x, y, z)
					chunk.set(x, y, z, cube)
				}
			}
		}

		// Add it to the chunk list
		chunks.add(chunk)
	}

	fun generateBatch(count: Int, origin: Vector3 = Vector3(0f, 0f, 0f)) {
		KtxAsync.launch {
			measureTimeMillis {
				val list = arrayListOf<Deferred<Unit>>()
				for (x in 0 until count) {
					for (z in 0 until count) {
						list.add(KtxAsync.async {
							generateChunk(origin + vec3(x * width, 0, z * depth))
						})
					}
				}
				list.awaitAll()
			}.also { elapsed ->
				println("Initial terrain generation of ${count * count} chunks took ${elapsed}ms")
			}

			chunks.forEach {
				withContext(meshingContext) { it.generateMesh() }
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

	fun update() {
		KtxAsync.launch {
			val list = arrayListOf<Deferred<Unit>>()

			chunks.forEach {
				// If the mesh was changed
				if (!it.isDirty) return@forEach

				// Re-generate the chunk's mesh
				list.add(async(meshingContext) { it.generateMesh() })

				// Re-generate the chunk's neighbors meshes
				it.neighbors.map { neighbor ->
					list.add(async(meshingContext) { neighbor.generateMesh() })
				}
			}

			list.awaitAll()
		}
		chunks.forEach {
			// If the mesh was changed
			if (!it.isDirty) return@forEach

			KtxAsync.launch(meshingContext) {
				// Re-generate the chunk's mesh
				it.generateMesh()

				// Re-generate the chunk's neighbors meshes
				//it.neighbors.map { neighbor -> neighbor.isDirty = true }
			}
		}
	}

}