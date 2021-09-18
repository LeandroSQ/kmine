package quevedo.soares.leandro.kmine.core.terrain

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.model.MeshPart
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import ktx.math.div
import ktx.math.plus
import quevedo.soares.leandro.kmine.core.enums.CubeFace
import quevedo.soares.leandro.kmine.core.models.PhysicsProperties
import quevedo.soares.leandro.kmine.core.utils.*
import com.badlogic.gdx.math.collision.BoundingBox

private const val MESH_ATTRIBUTES = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()

@Suppress("NOTHING_TO_INLINE")
class Chunk(position: Vector3, val width: Int, val height: Int, val depth: Int) {

	var renderable: Renderable? = null
		private set

	var indicesCount: Int = 0
		private set
	var verticesCount: Int = 0
		private set

	var isDirty = false
		private set

	var physics: PhysicsProperties? = null
		private set

	var boundingBox = BoundingBox()

	var position = position
	var quaternion = Quaternion(0f, 0f, 0f, 0f)
	val transform get() = Matrix4(this.position, this.quaternion, vec3(1, 1, 1))

	private val material = Material(
		TextureAttribute.createDiffuse(Cube.texture),
		BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
	)

	val cubeCount get() = width * height * depth

	val dimensions get() = vec3(this.width, this.height, this.depth)
	val center get() = this.position + this.dimensions / 2

	lateinit var cubes: CubeMatrix
		private set

	var isVisible = true

	init {
		// Fills the chunk with empty cubes
		this.cubes = CubeMatrix(width, height, depth, EMPTY)
	}

	// region Array get/set

	inline fun get(position: Vector3) = this.get(position.xInt, position.yInt, position.zInt)
	fun get(x: Int, y: Int, z: Int): Cube? {
		if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= depth) return null

		return this.cubes.get(x, y, z)
	}

	inline fun set(position: Vector3, cube: Cube?) = this.set(position.xInt, position.yInt, position.zInt, cube)
	fun set(x: Int, y: Int, z: Int, cube: Cube?) {
		this.cubes.set(x, y, z, cube)
		this.isDirty = true
	}

	inline fun isEmpty(position: Vector3) = this.isEmpty(position.xInt, position.yInt, position.zInt)

	inline fun isEmpty(x: Int, y: Int, z: Int) = this.get(x, y, z) == Chunk.EMPTY
	// endregion

	fun fill(cube: Cube?) {
		this.cubes = CubeMatrix(this.width, this.height, this.depth, cube)
		this.isDirty = true
	}

	inline fun getHighest(position: Vector3) = this.getHighest(position.xInt, position.zInt)
	fun getHighest(x: Int, z: Int): Cube? {
		for (y in 0 until this.height) {
			val cube = this.get(x, this.height - y, z)
			if (cube !== EMPTY) return cube
		}

		return null
	}

	/**
	 * Validates whether a face should be generated and baked into the chunk's mesh
	 * by verifying when theres a non-empty neighbouring cube that's not translucent
	 **/
	private fun shouldDrawFace(x: Int, y: Int, z: Int): Boolean {
		val cube = this.get(x, y, z)
		return cube == EMPTY || cube.isTranslucent
	}

	private fun addFace(mesh: MeshBuilder, cube: Cube, offset: FloatArray, face: CubeFace): Int {
		return when (face) {
			CubeFace.TOP -> mesh.addQuad(cube.topFace, cube.topNormal, offset, Cube.atlas, cube.textureMap.top)
			CubeFace.BOTTOM -> mesh.addQuad(cube.bottomFace, cube.bottomNormal, offset, Cube.atlas, cube.textureMap.bottom)
			CubeFace.LEFT -> mesh.addQuad(cube.leftFace, cube.leftNormal, offset, Cube.atlas, cube.textureMap.left)
			CubeFace.RIGHT -> mesh.addQuad(cube.rightFace, cube.rightNormal, offset, Cube.atlas, cube.textureMap.right)
			CubeFace.FRONT -> mesh.addQuad(cube.frontFace, cube.frontNormal, offset, Cube.atlas, cube.textureMap.front)
			CubeFace.BACK -> mesh.addQuad(cube.backFace, cube.backNormal, offset, Cube.atlas, cube.textureMap.back)
		}
	}

	private fun createPhysicsProperties() {
		// Defines the collision shape to be the generated mesh
		//val shape = btBvhTriangleMeshShape(Array.with(renderable!!.meshPart))
		// Creates a physics wrapper
		//this.physics = PhysicsProperties(shape, transform, mass = 1f)
	}

	private fun createRenderable(mesh: MeshPart) {
		// Creates the renderable
		this.renderable = Renderable().also {
			it.worldTransform.set(this.position, this.quaternion)
			it.meshPart.set(mesh)
			it.material = this.material
		}

		this.createPhysicsProperties()
	}

	fun generateMesh() {
		var verticesCount = 0

		val builder = MeshBuilder()
		builder.begin(MESH_ATTRIBUTES, GL20.GL_TRIANGLES)

		this.cubes.forEach { x, y, z, cube ->
			// Ignore empty cubes
			if (cube == EMPTY) return@forEach

			// Calculate the cube absolute position
			val offset = floatArrayOf(x.toFloat(), y.toFloat(), z.toFloat())

			// Append the faces
			if (shouldDrawFace(x, y + 1, z)) verticesCount += addFace(builder, cube, offset, CubeFace.TOP)
			if (shouldDrawFace(x, y - 1, z)) verticesCount += addFace(builder, cube, offset, CubeFace.BOTTOM)
			if (shouldDrawFace(x - 1, y, z)) verticesCount += addFace(builder, cube, offset, CubeFace.LEFT)
			if (shouldDrawFace(x + 1, y, z)) verticesCount += addFace(builder, cube, offset, CubeFace.RIGHT)
			if (shouldDrawFace(x, y, z + 1)) verticesCount += addFace(builder, cube, offset, CubeFace.FRONT)
			if (shouldDrawFace(x, y, z - 1)) verticesCount += addFace(builder, cube, offset, CubeFace.BACK)
		}

		val mesh = builder.end()
		// For some reason, the vertices count need to be divided by 2
		// And using mesh.numVertices simply doesn't works
		val meshPart = MeshPart("mesh", mesh, 0, verticesCount / 2, GL20.GL_TRIANGLES)

		// Stores the mesh attributes
		this.verticesCount = verticesCount
		this.indicesCount = verticesCount * 3
		this.boundingBox = mesh.calculateBoundingBox().apply {
			this.mul(Matrix4().apply {
				setTranslation(position)
			})
		}

		// Creates the renderable based on the generated mesh
		this.createRenderable(meshPart)

		this.isDirty = false
	}

	fun render(modelBatch: ModelBatch, environment: Environment) {
		this.renderable?.let {
			it.environment = environment
			modelBatch.render(it)
		}
	}

	fun dispose() {
		this.cubes.clear()
		this.renderable?.meshPart?.mesh?.dispose()
		this.renderable?.shader?.dispose()
		this.physics?.dispose()
	}

	companion object {

		val EMPTY = null

	}

}