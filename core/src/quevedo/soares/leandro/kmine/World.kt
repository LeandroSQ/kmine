package quevedo.soares.leandro.kmine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import quevedo.soares.leandro.kmine.cube.Cube
import quevedo.soares.leandro.kmine.cube.type.FurnaceCube

class World {

    private lateinit var environment: Environment

    private lateinit var modelBatch: ModelBatch

    private lateinit var cube: Cube

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

        this.cube = FurnaceCube().apply {
            createMesh()
        }
    }

    fun render(camera: Camera) {
        this.modelBatch.begin(camera)

        Gdx.graphics.gL20.glEnable(GL20.GL_TEXTURE_2D)
//        texture.bind()


        this.modelBatch.render(this.cube.modelInstance, this.environment)
        this.modelBatch.end()
    }

    fun dispose() {
        this.modelBatch.dispose()
        this.cube.dispose()
    }

}