package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.terrain.Chunk
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.terrain.FallingCube
import quevedo.soares.leandro.kmine.core.terrain.TerrainBuilder
import quevedo.soares.leandro.kmine.core.terrain.type.SandCube

class World {

    private lateinit var environment: Environment

    private lateinit var modelBatch: ModelBatch
    private lateinit var physics: Physics

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
                //chunk.setCubeAt(cube.position.x.toInt(), cube.position.y.toInt() + 1, cube.position.z.toInt(), FurnaceCube())
                //chunk.generateMesh()

                // Spawns a falling block
                val sandCube = SandCube(Vector3(cube.position.x, cube.position.y + 16f, cube.position.z))
                sandCube.generateMesh()
                physics.addEntity(sandCube)
                entities.add(sandCube)
            }
        }
    }

    private fun setupEnvironment() {
        this.environment = Environment().apply {
            set(ColorAttribute(ColorAttribute.AmbientLight, Color(0.5f, 0.5f, 0.5f, 1f)))
            add(DirectionalLight().set(Color(0.8f, 0.8f, 0.8f, 1f), -1f, -0.8f, -0.2f))
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