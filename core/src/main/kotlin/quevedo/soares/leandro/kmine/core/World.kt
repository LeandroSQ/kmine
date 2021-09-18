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
import ktx.math.plus
import ktx.math.vec3
import quevedo.soares.leandro.kmine.core.models.WorldInfoWrapper
import quevedo.soares.leandro.kmine.core.shader.MeshShader
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.terrain.FallingCube
import quevedo.soares.leandro.kmine.core.terrain.TerrainBuilder
import quevedo.soares.leandro.kmine.core.terrain.type.TorchCube
import quevedo.soares.leandro.kmine.core.utils.use
import quevedo.soares.leandro.kmine.core.utils.vec3

class World {

	private lateinit var environment: Environment

	private lateinit var modelBatch: ModelBatch
	private lateinit var physics: Physics

	private lateinit var meshShader: ShaderProgram

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
		this.physics = Physics()
		this.physics.init()

		this.meshShader = MeshShader.load()

		this.setupModel()
		this.setupEnvironment()
		this.setupShapeRenderer()

		this.testing()
	}

	private fun testing() {
		var maxPos = Vector3.Zero
		var maxChunk: Chunk = this.chunks.first()

		for (chunk in this.chunks) {
			for (x in 0 until chunk.xCount) {
				for (z in 0 until chunk.zCount) {
					chunk.getHighestCubeAt(x, z)?.position?.let { pos ->
						if (pos.y > maxPos.y) {
							maxPos = pos
							maxChunk = chunk
						}
					}
				}
			}
		}

		// Appends a furnace in the middle of the map
		var pos = maxPos + vec3(0, 1, 0)
		maxChunk.setCubeAt(pos, TorchCube())
		pos = maxChunk.absolutePosition(pos)
		maxChunk.generateMesh()
		environment.add(PointLight().apply {
			position.set(pos + vec3(0, 2, 0))
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

		this.chunks = TerrainBuilder.generateWorld(3)

		// Generate the chunks meshes
		this.chunks.forEach {
			it.generateMesh()
			this.physics.addStaticEntity(it)
		}
	}

	private fun setupShapeRenderer() {
		this.debugRenderer = ShapeRenderer().apply {
			color = Color(1f, 0f, 1f, 0.25f)
		}
	}

	private var test = false

	private fun renderChunkBoundaries() {
		this.debugRenderer.projectionMatrix = Game.player.camera.combined
		this.debugRenderer.use(ShapeRenderer.ShapeType.Line) {
			for (chunk in this.chunks) {
				val start = chunk.origin
				val center = chunk.center

				if (!test) {
					println("Start: ${start}, Dimensions: ${chunk.dimensions}")
				}

				this.debugRenderer.box(start.x, start.y, start.z, chunk.xCount.toFloat(), chunk.yCount.toFloat(), chunk.zCount.toFloat())
				this.debugRenderer.line(
					vec3(center.x, start.y, center.z),
					vec3(center.x, start.y + chunk.yCount, center.z),
				)
			}

			test = true
		}
	}

	fun render() {
		this.physics.update()

		this.sun.direction.rotate(Vector3.Z, (Gdx.graphics.deltaTime * Math.PI * 0.75f).toFloat())

		this.renderChunkBoundaries()

		this.meshShader.bind()
		this.modelBatch.begin(Game.player.camera)
		this.chunks.forEach {
			it.isVisible = Game.player.camera.frustum.boundsInFrustum(it.center, it.dimensions)
			if (it.isVisible) it.render(this.modelBatch, this.environment)
		}
		this.entities.forEach { it.render(this.modelBatch, this.environment) }
		this.modelBatch.end()
	}

	fun dispose() {
		this.modelBatch.dispose()
		this.physics.dispose()
		Cube.disposeTextures()
	}

}