package quevedo.soares.leandro.kmine.core.models

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController
import ktx.math.div

class PlayerPhysicsProperties (dimensions: Vector3, position: Vector3) {

	val controller: btKinematicCharacterController
	val ghostObject: btPairCachingGhostObject
	val shape: btCollisionShape

	init {
		val matrix = Matrix4().apply {
			setTranslation(position)
		}

//		this.shape = btBoxShape(dimensions)
		this.shape = btCapsuleShape(0.45f / 2f, 0.45f / 2f)

		this.ghostObject = btPairCachingGhostObject().apply {
			worldTransform = matrix
			collisionShape = shape
			collisionFlags = btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT
			friction = 0f
			//setContactStiffnessAndDamping(1f, 1f)
		}

		this.controller = btKinematicCharacterController(this.ghostObject, this.shape, 0.25f)
	}

	fun dispose() {
		this.ghostObject.dispose()
		this.controller.dispose()
		this.shape.dispose()
	}

}