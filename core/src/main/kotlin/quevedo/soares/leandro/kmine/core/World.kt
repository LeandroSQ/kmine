package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.PointLight
import com.badlogic.gdx.math.Vector3
import ktx.math.plus
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.terrain.FallingCube
import quevedo.soares.leandro.kmine.core.terrain.TerrainBuilder
import quevedo.soares.leandro.kmine.core.terrain.type.FurnaceCube
import quevedo.soares.leandro.kmine.core.terrain.type.TorchCube
import quevedo.soares.leandro.kmine.core.utils.vec3

class World {

	private lateinit var environment: Environment

	private lateinit var modelBatch: ModelBatch
	private lateinit var physics: Physics

	private lateinit var sun: DirectionalLight

	var chunks = arrayListOf<Chunk>()
	var entities = arrayListOf<FallingCube>()

	fun create() {
		this.physics = Physics()
		this.physics.init()

		this.setupModel()
		this.setupEnvironment()

		this.testing()
	}

	private fun testing() {
		this.chunks[this.chunks.size / 2].let { chunk ->
			chunk.getHighestCubeAt(chunk.xCount / 2, chunk.zCount / 2)?.let { cube ->
				// Appends a furnace in the middle of the map
				var pos = cube.position + vec3(0, 1, 0)
				chunk.setCubeAt(pos, TorchCube())
				pos = chunk.absolutePosition(pos)
				chunk.generateMesh()
				environment.add(PointLight().apply {
					position.set(pos)
					color.set(Color.CORAL)
					intensity = 5f
				})

				// Spawns a falling block
//                val sandCube = SandCube(cube.position + vec3(0, 16, 0))
//                sandCube.generateMesh()
//                physics.addEntity(sandCube)
//                entities.add(sandCube)
			}
		}
	}

	private fun setupEnvironment() {
		this.environment = Environment().apply {
//			 set(ColorAttribute(ColorAttribute.AmbientLight, Color(0.5f, 0.5f, 0.5f, 0.1f)))
			sun = DirectionalLight().set(Color(0.9f, 0.9f, 0.5f, 1f), -1f, -0.8f, -0.2f)
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