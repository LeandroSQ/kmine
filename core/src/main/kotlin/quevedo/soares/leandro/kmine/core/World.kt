package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.PointLight
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3
import ktx.math.plus
import quevedo.soares.leandro.kmine.core.shader.MeshShader
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.terrain.FallingCube
import quevedo.soares.leandro.kmine.core.terrain.TerrainBuilder
import quevedo.soares.leandro.kmine.core.terrain.type.TorchCube
import quevedo.soares.leandro.kmine.core.utils.vec3

class World {

	private lateinit var environment: Environment

	private lateinit var modelBatch: ModelBatch
	private lateinit var physics: Physics

	private lateinit var meshShader: ShaderProgram

	private lateinit var sun: DirectionalLight

	var chunks = arrayListOf<Chunk>()
	var entities = arrayListOf<FallingCube>()

	val verticesCount get()  = this.chunks.sumOf { it.verticesCount }
	val indicesCount get()  = this.chunks.sumOf { it.indicesCount }

	fun create() {
		this.physics = Physics()
		this.physics.init()

		this.meshShader = MeshShader.load()

		this.setupModel()
		this.setupEnvironment()

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
			position.set(pos + vec3(0, 1, 0))
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

	fun render(camera: Camera) {
		this.physics.update()

		this.sun.direction.rotate(Vector3.Z, (Gdx.graphics.deltaTime * Math.PI * 0.75f).toFloat())

		this.meshShader.bind()
		this.modelBatch.begin(camera)
		this.chunks.forEach { it.render(this.modelBatch, this.environment) }
		this.entities.forEach { it.render(this.modelBatch, this.environment) }
		this.modelBatch.end()
	}

	fun dispose() {
		this.modelBatch.dispose()
		this.physics.dispose()
		Cube.disposeTextures()
	}

}