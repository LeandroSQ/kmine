package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.PointLight
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import ktx.math.*
import quevedo.soares.leandro.kmine.core.models.WorldInfoWrapper
import quevedo.soares.leandro.kmine.core.shader.MeshShader
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.terrain.FallingCube
import quevedo.soares.leandro.kmine.core.terrain.TerrainGenerator
import quevedo.soares.leandro.kmine.core.terrain.type.TorchCube
import quevedo.soares.leandro.kmine.core.utils.Gizmo
import quevedo.soares.leandro.kmine.core.utils.use
import quevedo.soares.leandro.kmine.core.utils.vec3

class World {

	private lateinit var environment: Environment

	private lateinit var modelBatch: ModelBatch
	private lateinit var physics: Physics

	private lateinit var terrainGenerator: TerrainGenerator

	private lateinit var sun: DirectionalLight

	private lateinit var debugRenderer:ShapeRenderer

	var chunks = arrayListOf<Chunk>()
	var entities = arrayListOf<FallingCube>()

	fun calculateStatistics(): WorldInfoWrapper {
		var totalVerticesCount = 0
		var visibleVerticesCount = 0
		var totalIndicesCount = 0
		var visibleIndicesCount = 0
		val totalChunksCount = this.chunks.size
		var visibleChunksCount = 0

		for (chunk in this.chunks) {
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

	fun onCreate() {
		this.setupPhysics()
		this.setupTerrainGenerator()
		this.setupModel()
		this.setupEnvironment()
		this.setupShapeRenderer()

		this.testing()
	}

	private fun setupPhysics() {
		this.physics = Physics()
		this.physics.init()
	}

	private fun setupTerrainGenerator() {
		this.terrainGenerator = TerrainGenerator().apply {
			create()
		}
	}

	private fun testing() {
		var maxPos = Vector3.Zero
		var maxChunk: Chunk = this.chunks.first()

		for (chunk in this.chunks) {
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
		val pos = maxPos + vec3(0, 1, 0)
		maxChunk.set(pos, TorchCube())
		maxChunk.generateMesh()
		environment.add(PointLight().apply {
			position.set(pos + maxChunk.position + vec3(0f, 1.75f, 0f))
			color.set(Color.CORAL)
			intensity = 5f
		})
	}

	private fun setupEnvironment() {
		this.environment = Environment().apply {
			set(ColorAttribute(ColorAttribute.AmbientLight, Color(0.5f, 0.5f, 0.5f, 0.2f)))
			sun = DirectionalLight().set(Color(0.7f, 0.7f, 0.5f, 0.5f), -1f, -0.8f, -0.2f)
			add(sun)
		}
	}

	private fun setupModel() {
		this.modelBatch = ModelBatch()

		this.chunks = terrainGenerator.generateBatch(10)

		Gizmo.grid(Vector3(-0.5f, -0.5f, -0.5f), 20f, 16, Color(1f, 0.2f, 0.2f, 1f))

		// Generate the chunks meshes
		this.chunks.forEach {
			it.generateMesh()
			Gizmo.box(it.center + vec3(-0.5f, 0f, -0.5f), it.dimensions, Color(0f, 0f, 1f, 0.25f))
		}
	}

	private fun setupShapeRenderer() {
//		this.debugRenderer = ShapeRenderer()
	}

	private var test = false

	private fun renderChunkBoundaries() {
		this.debugRenderer.projectionMatrix = Game.player.camera.combined
		this.debugRenderer.use(ShapeRenderer.ShapeType.Line) {
			for (chunk in this.chunks) {
				val start = chunk.position
				val center = chunk.center

				if (!test) {
					println("Start: ${start}, Dimensions: ${chunk.dimensions}")
				}

				this.debugRenderer.color = Color(1f, 0f, 1f, 0.25f)
				this.debugRenderer.box(start.x, start.y, start.z, chunk.width.toFloat(), chunk.height.toFloat(), -chunk.depth.toFloat())
				this.debugRenderer.color = Color(0.1f, 0.3f, 1f, 0.25f)
				this.debugRenderer.line(
					vec3(center.x, start.y, center.z),
					vec3(center.x, start.y + chunk.height, center.z),
				)
				this.debugRenderer.color = Color(0f, 1f, 0f, 0.25f)
			}

			test = true
		}
	}

	fun render() {
		this.physics.update()

		this.sun.direction.rotate(Vector3.Z, (Gdx.graphics.deltaTime * Math.PI * 0.75f).toFloat())

//		this.renderChunkBoundaries()
		Gizmo.render(this.modelBatch, this.environment)

		this.modelBatch.begin(Game.player.camera)
		this.chunks.forEach {
			it.isVisible = Game.player.camera.frustum.boundsInFrustum(it.boundingBox)
			if (it.isVisible) it.render(this.modelBatch, this.environment)
		}
		//this.entities.forEach { it.render(this.modelBatch, this.environment) }
		this.modelBatch.end()
	}

	fun dispose() {
		Gizmo.dispose()
		this.modelBatch.dispose()
		this.physics.dispose()
		Cube.disposeTextures()
	}

}