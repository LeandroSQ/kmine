package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import quevedo.soares.leandro.kmine.core.shader.SkyboxShader
import quevedo.soares.leandro.kmine.core.utils.toFloatArray

private const val SIZE = 256f

class Skybox {
	val model: Model
	val modelInstance: ModelInstance
	val shader: ShaderProgram

	init {
		val material = Material(ColorAttribute.createDiffuse(Color.BLACK), DepthTestAttribute(0, false), IntAttribute(IntAttribute.CullFace, GL20.GL_NONE))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.ColorUnpacked).toLong()

		val builder = ModelBuilder()

		this.model = builder.createBox(SIZE, SIZE, SIZE, material, attributes)
		this.modelInstance = ModelInstance(this.model)

		this.shader = SkyboxShader.load()
	}

	fun render(modelBatch: ModelBatch) {

		//this.modelInstance.transform.position = Game.player.position
		shader.bind()
		shader.setUniformMatrix("u_projTrans", modelBatch.camera.projection);
		shader.setUniformMatrix("u_worldView", modelInstance.transform);
		shader.setUniform3fv("a_position", Game.player.position.cpy().toFloatArray(), 0, 3)

		model.meshes.first().render(shader, GL20.GL_TRIANGLES)
	}

}
