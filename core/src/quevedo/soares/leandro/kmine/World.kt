package quevedo.soares.leandro.kmine

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Vector3

class World {

    private lateinit var environment: Environment

    private lateinit var modelBatch: ModelBatch

    private var chunks = arrayListOf<Chunk>()

    fun create() {
        this.setupModel()
        this.setupEnvironment()
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
        this.chunks.forEach { it.generateMesh() }
    }

    fun render(camera: Camera) {
        this.modelBatch.begin(camera)
        this.chunks.forEach { it.render(this.modelBatch, this.environment) }
        this.modelBatch.end()
    }

    fun dispose() {
        this.modelBatch.dispose()
        this.chunks.forEach { it.dispose() }
        Chunk.disposeTextures()
    }

}