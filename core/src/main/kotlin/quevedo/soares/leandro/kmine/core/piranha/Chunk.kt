package quevedo.soares.leandro.kmine.core.piranha

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import ktx.math.div
import ktx.math.plus
import quevedo.soares.leandro.kmine.core.enums.CubeFace
import quevedo.soares.leandro.kmine.core.terrain.Cube
import quevedo.soares.leandro.kmine.core.utils.*

private const val MESH_ATTRIBUTES = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()

@Suppress("NOTHING_TO_INLINE")
class Chunk {

	var renderable: Renderable? = null
		private set

	var position: Vector3
		get() {
			val temp = Vector3.Zero
			this.renderable.worldTransform.getTranslation(temp)
			return temp
		}
		set(value) {
			this.renderable.worldTransform.getTranslation(value)
		}

	var quaternion: Quaternion
		get() {
			val temp = Quaternion()
			this.renderable.worldTransform.getRotation(temp)
			return temp
		}
		set(value) {
			this.renderable.worldTransform.set(this.position, value)
		}

	private val material = Material(
		TextureAttribute.createDiffuse(Cube.texture),
		BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
	)

	val width: Int
	val height: Int
	val depth: Int
	val cubeCount get() = width * height * depth

	val dimensions get() = vec3(this.width, this.height, this.depth)
	val center get() = this.position + this.dimensions / 2

	var cubes: ArrayList<Cube?> = arrayListOf()
		private set

	// region Constructors
	constructor(width: Int, height: Int, depth: Int) : super() {
		this.width = width
		this.height = height
		this.depth = depth
	}

	constructor(position: Vector3, width: Int, height: Int, depth: Int) : super() {
		this.position = position
		this.width = width
		this.height = height
		this.depth = depth
	}
	// endregion

	// region Array index conversion utility

	private inline fun indexToPosition(index: Int): Vector3 {
		val x = index % width
		val y = (index / width) % height
		val z = index / (width * height)
		return vec3(x, y, z)
	}

	private inline fun positionToIndex(x: Int, y: Int, z: Int): Int {
		return (width * height * z) + (width * y) + x
	}

	// endregion

	// region Array get/set

	inline fun get(position: Vector3) = this.get(position.xInt, position.yInt, position.zInt)
	fun get(x: Int, y: Int, z: Int): Cube? {
		val index = this.positionToIndex(x, y, z)
		return this.cubes[index]
	}

	inline fun set(position: Vector3, cube: Cube?) = this.set(position.xInt, position.yInt, position.zInt, cube)
	fun set(x: Int, y: Int, z: Int, cube: Cube?) {
		val index = this.positionToIndex(x, y, z)
		this.cubes[index] = cube
	}

	inline fun isEmpty(position: Vector3) = this.isEmpty(position.xInt, position.yInt, position.zInt)

	inline fun isEmpty(x: Int, y: Int, z: Int) = this.get(x, y, z) == Chunk.EMPTY
	// endregion

	fun fill(cube: Cube?) {
		this.cubes = arrayListOf()
		for (index in 0 until this.cubeCount) this.cubes.add(cube)
	}

	inline fun getHighest(position: Vector3) = this.getHighest(position.xInt, position.zInt)
	fun getHighest(x: Int, z: Int): Cube? {
		for (y in 1 until this.height) {
			val cube = this.get(x, y, z)
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

	private fun addFace(mesh: MeshBuilder, cube: Cube, offset: FloatArray, face: CubeFace) {
		when (face) {
			CubeFace.TOP -> mesh.addQuad(cube.topFace, cube.topNormal, offset, Cube.atlas, cube.textureMap.top)
			CubeFace.BOTTOM -> mesh.addQuad(cube.bottomFace, cube.bottomNormal, offset, Cube.atlas, cube.textureMap.bottom)
			CubeFace.LEFT -> mesh.addQuad(cube.leftFace, cube.leftNormal, offset, Cube.atlas, cube.textureMap.left)
			CubeFace.RIGHT -> mesh.addQuad(cube.rightFace, cube.rightNormal, offset, Cube.atlas, cube.textureMap.right)
			CubeFace.FRONT -> mesh.addQuad(cube.frontFace, cube.frontNormal, offset, Cube.atlas, cube.textureMap.front)
			CubeFace.BACK -> mesh.addQuad(cube.backFace, cube.backNormal, offset, Cube.atlas, cube.textureMap.back)
		}
	}

	fun bake() {
		val builder = MeshBuilder()
		builder.begin(MESH_ATTRIBUTES)
		for (index in 0 until this.cubeCount) {
			// Fetch the cube, ignoring EMPTY cubes
			val cube = this.cubes[index] ?: continue

			// Define the cube relative position
			val pos = this.indexToPosition(index)
			val x = pos.xInt
			val y = pos.yInt
			val z = pos.zInt

			// Calculate the cube absolute position
			val offset = (pos + this.position).toFloatArray()

			// Append the faces
			if (shouldDrawFace(x, y + 1, z)) addFace(builder, cube, offset, CubeFace.TOP)
			if (shouldDrawFace(x, y - 1, z)) addFace(builder, cube, offset, CubeFace.BOTTOM)
			if (shouldDrawFace(x - 1, y, z)) addFace(builder, cube, offset, CubeFace.LEFT)
			if (shouldDrawFace(x + 1, y, z)) addFace(builder, cube, offset, CubeFace.RIGHT)
			if (shouldDrawFace(x, y, z + 1)) addFace(builder, cube, offset, CubeFace.FRONT)
			if (shouldDrawFace(x, y, z - 1)) addFace(builder, cube, offset, CubeFace.BACK)
		}
		this.meshPart = builder.meshPart
	}

	companion object {

		val EMPTY = null

	}

}