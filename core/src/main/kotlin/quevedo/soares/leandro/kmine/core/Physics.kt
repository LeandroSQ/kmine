package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState

internal const val ENABLED = false

class Physics {

	private lateinit var collisionConfig: btCollisionConfiguration
	private lateinit var dispatcher: btDispatcher
	private lateinit var sequentialImpulseConstraintSolver: btSequentialImpulseConstraintSolver
	private lateinit var world: btDiscreteDynamicsWorld
	private lateinit var btSweep3: btAxisSweep3

	fun init() {
		if (!ENABLED) return

		this.collisionConfig = btDefaultCollisionConfiguration()
		this.dispatcher = btCollisionDispatcher(this.collisionConfig)
		this.sequentialImpulseConstraintSolver = btSequentialImpulseConstraintSolver()
		btSweep3 = btAxisSweep3(Vector3(-1000f, -1000f, -1000f), Vector3(1000f, 1000f, 1000f))
		this.world = btDiscreteDynamicsWorld(dispatcher, btSweep3, sequentialImpulseConstraintSolver, collisionConfig)
	}

	fun update() {
		if (!ENABLED) return

		world.stepSimulation(Gdx.graphics.deltaTime, 5)
	}

	fun dispose() {
		if (!ENABLED) return

		/*this.collisionConfig.dispose()
		this.dispatcher.dispose()
		this.btSweep3.dispose()
		this.objects.forEach { it.dispose() }*/
	}

}