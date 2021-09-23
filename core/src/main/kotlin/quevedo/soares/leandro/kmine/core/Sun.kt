package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import ktx.math.vec3
import quevedo.soares.leandro.kmine.core.utils.Gizmo
import quevedo.soares.leandro.kmine.core.utils.createQuad
import quevedo.soares.leandro.kmine.core.utils.position

class Sun(environment: Environment) {

	private var light: DirectionalLight = DirectionalLight().set(Color(0.7f, 0.7f, 0.5f, 0.5f), -1f, -0.8f, -0.2f)
	private var model: Model
	private var modelInstance: ModelInstance
	private var arrow: ModelInstance

	init {
		// Creates the light
		environment.add(this.light)

		// Creates the quad
		val builder = ModelBuilder()

		val material = Material(ColorAttribute.createDiffuse(Color.CORAL))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.ColorPacked).toLong()

		this.model = builder.createQuad(2f, material, attributes)
		this.modelInstance = ModelInstance(this.model).apply {
			transform.setTranslation(light.direction)

		}

		val arrowModel = Gizmo.line(Vector3(0f, 0f, 0f), vec3(0f, 0f, 1.5f), Color.BLUE, true)
		this.arrow = ModelInstance(arrowModel).apply {
			transform.position = modelInstance.transform.position
		}
	}

	fun render(modelBatch: ModelBatch) {
		// Rotates the light
		this.light.direction.rotate(Vector3.Z, (Gdx.graphics.deltaTime * Math.PI * 1.75f).toFloat())
		this.modelInstance.transform.apply {
			position = light.direction
			arrow.transform.position = position
		}

		modelBatch.render(this.modelInstance)
		modelBatch.render(this.arrow)
	}

}