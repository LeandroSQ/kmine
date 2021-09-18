package quevedo.soares.leandro.kmine.core.models

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState

class PhysicsProperties	(val shape: btCollisionShape, transform: Matrix4, mass: Float, localInertia: Vector3 = Vector3.Zero) {

	val collider: btCollisionObject
	val rigidBody: btRigidBody
	val constructionInfo: btRigidBody.btRigidBodyConstructionInfo
	val motionState: btMotionState

	init {
		// Creates the collider
		this.collider = btCollisionObject().apply {
			collisionShape = shape
			worldTransform = transform
		}

		// Creates the default motion state
		this.motionState = btDefaultMotionState().apply {
			setWorldTransform(transform)
		}

		// Creates the rigid body
		this.constructionInfo = btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, localInertia)
		this.rigidBody = btRigidBody(constructionInfo).apply {
			friction = 0f
		}
	}

	fun dispose() {
		this.constructionInfo.dispose()
		this.motionState.dispose()
		this.rigidBody.dispose()
		this.collider.dispose()
		this.shape.dispose()
	}
}