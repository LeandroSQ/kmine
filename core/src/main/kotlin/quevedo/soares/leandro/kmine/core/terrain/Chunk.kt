package quevedo.soares.leandro.kmine.core.terrain

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState
import ktx.math.minus
import ktx.math.plus
import quevedo.soares.leandro.kmine.core.PhysicsEntity
import quevedo.soares.leandro.kmine.core.enums.CubeTexture
import quevedo.soares.leandro.kmine.core.utils.*

@Suppress("NOTHING_TO_INLINE")
class Chunk : PhysicsEntity {

	private lateinit var model: Model
	private lateinit var modelInstance: ModelInstance

	var verticesCount: Int = 0
		private set

	var indicesCount: Int = 0
		private set

	var cubes: ArrayList<ArrayList<ArrayList<Cube?>>> = arrayListOf()

	override var collisionObject: btCollisionObject? = null
	override var rigidBody: btRigidBody? = null

	var origin = Vector3.Zero

	// region Utilities
	val xCount get() = this.cubes.size
	val yCount get() = this.cubes.first().size
	val zCount get() = this.cubes.first().first().size
	// endregion

	fun fillWith(size: Int, altitude: Int, cube: Cube? = null) {
		val xBuffer = arrayListOf<ArrayList<ArrayList<Cube?>>>()
		for (x in 0 until size) {
			val yBuffer = arrayListOf<ArrayList<Cube?>>()

			for (y in 0 until altitude) {
				val zBuffer = arrayListOf<Cube?>()
				for (z in 0 until size) {
					zBuffer.add(cube)
				}

				yBuffer.add(zBuffer)
			}

			xBuffer.add(yBuffer)
		}

		this.cubes = xBuffer
	}

	fun getCubeAt(position: Vector3): Cube? {
		if (position.xInt >= this.xCount || position.yInt >= this.yCount || position.zInt >= this.zCount || position.xInt < 0 || position.yInt < 0 || position.zInt < 0) return null
		return cubes[position.xInt][position.yInt][position.zInt]
	}

	fun setCubeAt(position: Vector3, cube: Cube?) {
		cube?.position = position
		this.cubes[position.x.toInt()][position.y.toInt()][position.z.toInt()] = cube
	}

	fun absolutePosition(relativePosition: Vector3) = relativePosition + this.origin
	fun relativePosition(absolutePosition: Vector3) = absolutePosition - this.origin

	private inline fun isCubeEmptyAt(x: Int, y: Int, z: Int): Boolean {
		val cube = this.getCubeAt(vec3(x, y, z))
		return cube == null || cube.isTranslucent
	}

	fun getHighestCubeAt(x: Int, z: Int): Cube? {
		for (y in 1 until this.cubes[x].size) {
			val cube = this.cubes[x][this.cubes[x].size - y][z]
			if (cube != null) return cube
		}

		return null
	}

	fun generateMesh() {
		val material = Material(TextureAttribute.createDiffuse(Cube.texture), BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()

		// if (this.model::isInitialized) this.model.dispose()
		this.model = ModelBuilder().run {
			begin()

			part("mesh", GL20.GL_TRIANGLES, attributes, material).apply {
				for (x in 0 until cubes.size) {
					for (y in 0 until cubes[x].size) {
						for (z in 0 until cubes[x][y].size) {
							// Ignore empty cubes
							val cube = cubes[x][y][z] ?: continue

							// Define the cube position
							val offset = floatArrayOf(x.toFloat(), y.toFloat(), z.toFloat())

							// Top
							if (isCubeEmptyAt(x, y + 1, z)) addQuad(cube.topFace, cube.topNormal, offset, Cube.atlas, cube.textureMap.top)
							// Bottom
							if (isCubeEmptyAt(x, y - 1, z)) addQuad(cube.bottomFace, cube.bottomNormal, offset, Cube.atlas, cube.textureMap.bottom)
							// Left
							if (isCubeEmptyAt(x - 1, y, z)) addQuad(cube.leftFace, cube.leftNormal, offset, Cube.atlas, cube.textureMap.left)
							// Right
							if (isCubeEmptyAt(x + 1, y, z)) addQuad(cube.rightFace, cube.rightNormal, offset, Cube.atlas, cube.textureMap.right)
							// Front
							if (isCubeEmptyAt(x, y, z + 1)) addQuad(cube.frontFace, cube.frontNormal, offset, Cube.atlas, cube.textureMap.front)
							// Back
							if (isCubeEmptyAt(x, y, z - 1)) addQuad(cube.backFace, cube.backNormal, offset, Cube.atlas, cube.textureMap.back)
						}
					}
				}
			}

			end()
		}

		this.verticesCount = this.model.meshParts.sumOf { it.mesh.numVertices }
		this.indicesCount = this.model.meshParts.sumOf { it.mesh.numIndices }

		this.modelInstance = ModelInstance(this.model)
		this.modelInstance.transform.setTranslation(this.origin)

		//if (this.collisionObject != null || this.rigidBody != null) this.disposeCollisionObject()
		//generateCollisionObject()
	}

	private fun generateCollisionObject() {
		// Abstracts the transform
		val transform = Matrix4()
		transform.setTranslation(this.origin)
		transform.setToScaling(Vector3(1f, 1f, 1f))

		this.collisionObject = btCollisionObject().apply {
			collisionShape = btBvhTriangleMeshShape(model.meshParts)
			worldTransform = transform
		}

		// Create motion state
		val motionState = btDefaultMotionState()
		motionState.setWorldTransform(transform)

		// Create rigid body
		val constructionInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, collisionObject?.collisionShape, Vector3.Zero)
		this.rigidBody = btRigidBody(constructionInfo).apply {
			friction = 0f
		}
	}

	fun render(modelBatch: ModelBatch, environment: Environment) {
		modelBatch.render(this.modelInstance, environment)
	}

	private fun disposeCollisionObject() {
		this.collisionObject?.collisionShape?.dispose()
		this.collisionObject?.dispose()
		this.rigidBody?.dispose()
	}

	override fun dispose() {
		this.disposeCollisionObject()
		this.model.dispose()
	}

}