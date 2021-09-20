package quevedo.soares.leandro.kmine.core.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import ktx.math.*
import quevedo.soares.leandro.kmine.core.utils.vec3

object Gizmo {

	private var models = arrayListOf<ModelInstance>()

	fun grid(position: Vector3, cellSize: Float, cellCount: Int, color: Color) {
		val material = Material(ColorAttribute.createDiffuse(color))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.ColorPacked).toLong()
		val model = ModelBuilder().createLineGrid(cellCount, cellCount, cellSize, cellSize, material, attributes)

		this.models.add(ModelInstance(model).apply {
			val bb = BoundingBox()
			model.calculateBoundingBox(bb)

			val dimensions = Vector3()
			bb.getDimensions(dimensions)

			transform.setTranslation(position + dimensions / 4f)
		})
	}

	fun directionArrows(center: Vector3, dimensions: Vector3) {
		this.line(center + dimensions * Vector3.Z, center + dimensions * Vector3.Z * 1.5f, Color.BLUE)
		this.line(center + dimensions * Vector3.X, center + dimensions * Vector3.X * 1.5f, Color.GREEN)
		this.line(center + dimensions * Vector3.Y , center + dimensions * Vector3.Y * 1.5f, Color.RED)
	}

	fun line(start: Vector3, end: Vector3, color: Color, ignore: Boolean = false): Model {
		val material = Material(ColorAttribute.createDiffuse(color))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.ColorPacked).toLong()
		val model = ModelBuilder().createArrow(start, end, material, attributes)

		if (!ignore) this.models.add(ModelInstance(model))
		return model
	}

	fun lines(center: Vector3, size: Vector3, color: Color) {
		(center - vec3(size.x, size.y, size.z) / 2).let { line(it, it + Vector3.Y * size, color) }
		(center - vec3(-size.x, size.y, size.z) / 2).let { line(it, it + Vector3.Y * size, color) }
		(center - vec3(size.x, size.y, -size.z + 2) / 2).let { line(it, it + Vector3.Y * size, color) }
		(center - vec3(-size.x, size.y, -size.z + 2) / 2).let { line(it, it + Vector3.Y * size, color) }
	}

	fun box(boundingBox: BoundingBox, color: Color) {
		val dimensions = Vector3()
		boundingBox.getDimensions(dimensions)

		this.box(boundingBox.min + dimensions / 2, boundingBox.max - dimensions / 2, color)
	}

	fun box(position: Vector3, size: Vector3, color: Color, filled: Boolean = false, ignore: Boolean = false): ModelInstance {
		val material = Material(ColorAttribute.createDiffuse(color))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.ColorPacked).toLong()
		val primitive = if (filled) GL20.GL_TRIANGLES else GL20.GL_LINES
		val model = ModelBuilder().createBox(size.x, size.y, size.z, primitive, material, attributes)
		val instance = ModelInstance(model).apply {
			transform.set(position, Quaternion())
		}

		if (!ignore) this.models.add(instance)
		return instance
	}

	fun shpere(position: Vector3, radius: Float, color: Color, filled: Boolean = true) {
		val material = Material(ColorAttribute.createDiffuse(color))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.ColorPacked).toLong()
		val primitive = if (filled) GL20.GL_TRIANGLES else GL20.GL_LINES
		val model = ModelBuilder().createSphere(radius, radius, radius, 3, 3, primitive, material, attributes)

		this.models.add(ModelInstance(model).apply {
			transform.set(position, Quaternion())
		})
	}

	fun render(modelBatch: ModelBatch, environment: Environment) {
		this.models.forEach { modelBatch.render(it, environment) }
	}

	fun dispose() {
		this.models.forEach { it.model.dispose() }
	}

}