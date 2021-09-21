package quevedo.soares.leandro.kmine.core.terrain

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState
import quevedo.soares.leandro.kmine.core.utils.addQuad

const val GRAVITY = 0.1f

open class FallingCube {

	/*private lateinit var model: Model
	private lateinit var modelInstance: ModelInstance
	//override var collisionObject: btCollisionObject? = null
	//override var rigidBody: btRigidBody? = null
	var isGrounded = false

	constructor() : super() {

	}

	constructor(position: Vector3) : super(position) {

	}

	private fun generateCollisionObject(){
		val mass = 1f

		// Abstracts the transform
		val transform = Matrix4()
		transform.setTranslation(this.position)
		transform.setToScaling(Vector3(1f, 1f, 1f))

		// Create the collision object and collision shape
		this.collisionObject = btCollisionObject().apply {
			collisionShape = btBoxShape(Vector3(0.5f, 0.5f, 0.5f))
			worldTransform = transform
		}

		// Create motion state
		val motionState = btDefaultMotionState()
		motionState.setWorldTransform(transform)

		// Calculate local inertia
		val dynamicInertia = Vector3(0f, 0f, 0f)
		collisionObject?.collisionShape?.calculateLocalInertia(mass, dynamicInertia)

		// Create rigid body
		val constructionInfo = btRigidBody.btRigidBodyConstructionInfo(mass, motionState, collisionObject?.collisionShape, dynamicInertia)
		this.rigidBody = btRigidBody(constructionInfo).apply {
			activationState = 4
			contactProcessingThreshold = 0f
			restitution = 0f
			setDamping(0.9f, 0.9f)
			linearFactor = Vector3(1f, 1f, 1f)
			angularFactor = Vector3(0f, 0f, 0f)
			contactCallbackFlag = 2
			contactCallbackFilter = 2
		}
	}

	fun render(modelBatch: ModelBatch, environment: Environment) {
		if (!this.isGrounded) {
			this.position.add(Vector3(0f, -1f * GRAVITY, 0f))
			this.modelInstance.transform.setTranslation(this.position)
			this.collisionObject?.worldTransform?.setTranslation(this.position)
		}

		modelBatch.render(modelInstance, environment)
	}

	fun generateMesh() {
		val material = Material(TextureAttribute.createDiffuse(texture))
		val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()

		//if (this.model != null) this.model.dispose()
		this.model = ModelBuilder().run {
			begin()

			part("mesh", GL20.GL_TRIANGLES, attributes, material).apply {
				val offset = floatArrayOf(0f, 0f, 0f)

				// Top
				addQuad(topFace, topNormal, offset, atlas, textureMap.top)
				// Bottom
				addQuad(bottomFace, bottomNormal, offset, atlas, textureMap.bottom)
				// Left
				addQuad(leftFace, leftNormal, offset, atlas, textureMap.left)
				// Right
				addQuad(rightFace, rightNormal, offset, atlas, textureMap.right)
				// Front
				addQuad(frontFace, frontNormal, offset, atlas, textureMap.front)
				// Back
				addQuad(backFace, backNormal, offset, atlas, textureMap.back)
			}

			end()
		}
		this.modelInstance = ModelInstance(this.model)
		this.modelInstance.transform.setTranslation(this.position)

		if (this.collisionObject != null || this.rigidBody != null) this.disposeCollisionObject()
		this.generateCollisionObject()
	}

	private fun disposeCollisionObject() {
		this.collisionObject?.collisionShape?.dispose()
		this.collisionObject?.dispose()
		this.rigidBody?.dispose()
	}

	override fun dispose() {
		this.disposeCollisionObject()
		this.model.dispose()
	}*/

}