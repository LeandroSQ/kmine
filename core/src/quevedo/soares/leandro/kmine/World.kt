package quevedo.soares.leandro.kmine

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import quevedo.soares.leandro.kmine.cube.Cube

class World {

    private lateinit var environment: Environment

    private lateinit var modelBatch: ModelBatch

    private lateinit var chunk: CubeChunk

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

        this.chunk = CubeChunk()
        this.chunk.generate()
    }

    fun render(camera: Camera) {
        this.modelBatch.begin(camera)
        this.chunk.render(this.modelBatch, this.environment)
        this.modelBatch.end()
    }

    fun dispose() {
        this.modelBatch.dispose()
        this.chunk.dispose()
    }

}