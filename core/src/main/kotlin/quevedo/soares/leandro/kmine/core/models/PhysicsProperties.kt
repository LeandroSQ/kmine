package quevedo.soares.leandro.kmine.core.models

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState
import quevedo.soares.leandro.kmine.core.Game

class PhysicsProperties	(val shape: btCollisionShape, val isStatic: Boolean, position: Vector3, mass: Float, localInertia: Vector3 = Vector3(0f, 0f, 0f)) {

	val collider: btCollisionObject
	val rigidBody: btRigidBody
	val constructionInfo: btRigidBody.btRigidBodyConstructionInfo
	val motionState: btMotionState

	init {
		val matrix = Matrix4().apply {
			setTranslation(position)
		}

		// Creates the collider
		this.collider = btCollisionObject().apply {
			collisionShape = shape
			worldTransform = matrix
		}

		// Creates the default motion state
		this.motionState = btDefaultMotionState().apply {
			setWorldTransform(matrix)
		}

		// Creates the rigid body
		this.constructionInfo = btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, localInertia)
		this.rigidBody = btRigidBody(constructionInfo).apply {
			friction = 0f
		}
	}

	fun dispose() {
		Game.physics.remove(this)

		this.constructionInfo.dispose()
		this.motionState.dispose()
		this.rigidBody.dispose()
		this.collider.dispose()
		this.shape.dispose()
	}
}