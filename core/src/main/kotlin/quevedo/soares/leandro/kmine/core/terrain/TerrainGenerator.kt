package quevedo.soares.leandro.kmine.core.terrain

import com.badlogic.gdx.math.Vector3
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktx.math.plus
import quevedo.soares.leandro.kmine.core.terrain.biome.Biome
import quevedo.soares.leandro.kmine.core.terrain.biome.DefaultBiome
import quevedo.soares.leandro.kmine.core.utils.*
import java.util.*
import kotlin.math.ceil

private const val WIDTH = 16
private const val HEIGHT = 64
private const val DEPTH = 16
private val SEED = Random().nextLong() + System.currentTimeMillis()

@Suppress("NOTHING_TO_INLINE")
class TerrainGenerator {

	internal lateinit var openSimplexNoise: OpenSimplexNoise

	private var biomes = arrayListOf<Biome>()

	fun create() {
		this.openSimplexNoise = OpenSimplexNoise(SEED)
		this.biomes.add(DefaultBiome())
	}

	inline fun getBiomeAt(position: Vector3) = this.getBiomeAt(position.xInt, position.zInt)
	fun getBiomeAt(x: Int, z: Int): Biome {
		// Calculates the divider, to scarse the x-z values
		// Therefore making the biome transitioning less often
		val divider = (WIDTH * HEIGHT * DEPTH) / 3f

		// Apply the noise function
		val index = ceil(this.openSimplexNoise.noise2D(x / divider, z / divider, min = 0f, max = biomes.size.toFloat()))

		// Get the selected biome for the position
		return this.biomes[clamp(index.toInt(), 0, this.biomes.size - 1)]
	}

	fun generateChunk(position: Vector3): Chunk {
		// Initializes the chunk
		val chunk = Chunk(position, WIDTH, HEIGHT, DEPTH)

		// Fetch the biome to be used at the given position
		val biome = getBiomeAt(position)
		biome.generate(this, chunk)

		return chunk
	}

	fun generateBatch(count: Int, origin: Vector3 = Vector3.Zero): ArrayList<Chunk> {
		val list = arrayListOf<Chunk>()

		runBlocking {

			for (x in 0 until count) {
				for (z in 0 until count) {
					launch {
						list.add(generateChunk(origin + vec3(x * WIDTH, 0, z * DEPTH)))
					}
				}
			}

		}

		return list
	}

	fun dispose() {
		this.biomes.forEach { it.dispose() }
		this.biomes.clear()
	}

}