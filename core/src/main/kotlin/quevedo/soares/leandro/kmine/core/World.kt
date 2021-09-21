package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.PointLight
import com.badlogic.gdx.math.Vector3
import ktx.math.plus
import ktx.math.vec3
import quevedo.soares.leandro.kmine.core.models.WorldInfoWrapper
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.terrain.Terrain
import quevedo.soares.leandro.kmine.core.terrain.type.TorchCube
import quevedo.soares.leandro.kmine.core.utils.Gizmo
import quevedo.soares.leandro.kmine.core.utils.use
import quevedo.soares.leandro.kmine.core.utils.vec3

class World {

	private lateinit var environment: Environment
	private lateinit var modelBatch: ModelBatch
	private lateinit var sun: Sun
	private lateinit var skybox: Skybox
	lateinit var terrain: Terrain

	// region Setup
	fun onCreate() {
		this.setupTerrain()
		this.setupModel()
		this.setupEnvironment()
		this.setupSun()
		this.setupSkybox()

		this.testing()
	}

	private fun setupTerrain() {
		this.terrain = Terrain()
		this.terrain.create()
		this.terrain.generateBatch(10)
	}

	private fun setupEnvironment() {
		this.environment = Environment().apply {
			set(ColorAttribute(ColorAttribute.AmbientLight, Color(0.5f, 0.5f, 0.5f, 0.2f)))
		}
	}

	private fun setupSun() {
		this.sun = Sun(this.environment)
	}

	private fun setupSkybox() {
		this.skybox = Skybox()
	}

	private fun testing() {
		var maxPos = Vector3(0f, 0f, 0f)
		var maxChunk: Chunk? = null

		for (chunk in this.terrain.chunks) {
			for (x in 0 until chunk.width) {
				for (z in 0 until chunk.depth) {
					chunk.getHighest(x, z)?.position?.let { pos ->
						if (pos.y > maxPos.y) {
							maxPos = pos
							maxChunk = chunk
						}
					}
				}
			}
		}

		// Appends a furnace in the middle of the map
		maxChunk?.let {
			val pos = maxPos + vec3(0, 1, 0)
			it.set(pos, TorchCube())
			it.generateMesh()
			environment.add(PointLight().apply {
				position.set(pos + it.position + vec3(0f, 1.75f, 0f))
				color.set(Color.CORAL)
				intensity = 5f
			})
		}

	}

	private fun setupModel() {
		this.modelBatch = ModelBatch()

		Gizmo.grid(Vector3(-0.5f, -0.5f, -0.5f), 16f, 20, Color(1f, 0.2f, 0.2f, 1f))

		// Generate the chunks meshes
		this.terrain.chunks.forEach {
			it.generateMesh()
			Gizmo.box(it.center + vec3(-0.5f, -0.5f, -0.5f), it.dimensions, Color(0f, 0f, 1f, 0.25f))
		}
	}
	// endregion

	fun calculateStatistics(): WorldInfoWrapper {
		var totalVerticesCount = 0
		var visibleVerticesCount = 0
		var totalIndicesCount = 0
		var visibleIndicesCount = 0
		val totalChunksCount = this.terrain.chunks.size
		var visibleChunksCount = 0

		for (chunk in this.terrain.chunks) {
			totalIndicesCount += chunk.indicesCount
			totalVerticesCount += chunk.verticesCount

			if (chunk.isVisible) {
				visibleChunksCount++
				visibleIndicesCount += chunk.indicesCount
				visibleVerticesCount += chunk.verticesCount
			}
		}

		return WorldInfoWrapper(totalVerticesCount, visibleVerticesCount, totalIndicesCount, visibleIndicesCount, totalChunksCount, visibleChunksCount)
	}

	fun render() {
		// Render Gizmos
		Gizmo.render(this.modelBatch, this.environment)

		this.terrain.update()

		this.modelBatch.use(Game.player.camera) {
			this.skybox.render(this.modelBatch)

			Gdx.gl.glEnable(GL20.GL_CULL_FACE)
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)

			this.sun.render(this.modelBatch)
			this.terrain.render(this.modelBatch, this.environment)

			Gdx.gl.glDisable(GL20.GL_CULL_FACE)
			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
		}
	}

	fun dispose() {
		Gizmo.dispose()
		this.modelBatch.dispose()
		this.terrain.dispose()
		Cube.disposeTextures()
	}

}