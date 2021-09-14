package quevedo.soares.leandro.kmine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder


class World {

    private lateinit var environment: Environment

    private lateinit var modelBatch: ModelBatch
    private lateinit var model: Model
    private val modelInstance by lazy { ModelInstance(this.model) }

    private lateinit var mesh: Mesh

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

        val vertices = floatArrayOf(

            // Front face
            -1.0f, -1.0f,  1.0f,   0f, 0f, 1f,
            1.0f,  -1.0f,  1.0f,   0f, 0f, 1f,
            1.0f,   1.0f,  1.0f,   0f, 0f, 1f,
            -1.0f,  1.0f,  1.0f,   0f, 0f, 1f,

            // Back face
            -1.0f, -1.0f, -1.0f,   0f, 0f, -1f,
            -1.0f,  1.0f, -1.0f,   0f, 0f, -1f,
            1.0f,  1.0f, -1.0f,    0f, 0f, -1f,
            1.0f, -1.0f, -1.0f,    0f, 0f, -1f,

            // Top face
            -1.0f,  1.0f, -1.0f,   0f, 1f, 0f,
            -1.0f,  1.0f,  1.0f,   0f, 1f, 0f,
            1.0f,  1.0f,  1.0f,    0f, 1f, 0f,
            1.0f,  1.0f, -1.0f,    0f, 1f, 0f,

            // Bottom face
            -1.0f, -1.0f, -1.0f,   0f, -1f, 0f,
            1.0f, -1.0f, -1.0f,    0f, -1f, 0f,
            1.0f, -1.0f,  1.0f,    0f, -1f, 0f,
            -1.0f, -1.0f,  1.0f,   0f, -1f, 0f,

            // Right face
            1.0f, -1.0f, -1.0f,    1f, 0f, 0f,
            1.0f,  1.0f, -1.0f,    1f, 0f, 0f,
            1.0f,  1.0f,  1.0f,    1f, 0f, 0f,
            1.0f, -1.0f,  1.0f,    1f, 0f, 0f,

            // Left face
            -1.0f, -1.0f, -1.0f,   -1f, 0f, 0f,
            -1.0f, -1.0f,  1.0f,   -1f, 0f, 0f,
            -1.0f,  1.0f,  1.0f,   -1f, 0f, 0f,
            -1.0f,  1.0f, -1.0f,   -1f, 0f, 0f
        )
        val indices = shortArrayOf(
            0,  1,  2,      0,  2,  3,    // front
            4,  5,  6,      4,  6,  7,    // back
            8,  9,  10,     8,  10, 11,   // top
            12, 13, 14,     12, 14, 15,   // bottom
            16, 17, 18,     16, 18, 19,   // right
            20, 21, 22,     20, 22, 23    // left
        )

        val modelBuilder = ModelBuilder()
        modelBuilder.begin()

        val material = Material(ColorAttribute.createDiffuse(Color(0.2f, 1f, 0.2f, 1f)))

        val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        val meshBuilder = modelBuilder.part("mesh", GL20.GL_TRIANGLES, attributes, material)
        val meshPiece = Mesh(true, vertices.size, indices.size, VertexAttribute.Position(), VertexAttribute.Normal()).apply {
            setVertices(vertices)
            setIndices(indices)
        }
        meshBuilder.addMesh(meshPiece)

        this.model = modelBuilder.end()
    }

    fun render(camera: Camera) {
        this.modelBatch.begin(camera)
        this.modelBatch.render(this.modelInstance, this.environment)
        this.modelBatch.end()
    }

    fun dispose() {
        this.modelBatch.dispose()
        this.model.dispose()
//        this.mesh.dispose()
    }

}