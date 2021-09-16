package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver

interface PhysicsEntity {
	var collisionObject: btCollisionObject?
	var rigidBody: btRigidBody?
	val collisionShape: btCollisionShape? get() = this.collisionObject?.collisionShape

	fun dispose() {
		this.collisionShape?.dispose()
		this.collisionObject?.dispose()
		this.rigidBody?.dispose()
	}
}

internal const val ENABLED = false

class Physics {
	private val objects = arrayListOf<PhysicsEntity>()

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

	fun addStaticEntity(entity: PhysicsEntity) {
		if (!ENABLED) return

		world.addRigidBody(entity.rigidBody, btBroadphaseProxy.CollisionFilterGroups.StaticFilter, btBroadphaseProxy.CollisionFilterGroups.CharacterFilter or btBroadphaseProxy.CollisionFilterGroups.DefaultFilter)
		this.objects.add(entity)
	}

	fun addEntity(entity: PhysicsEntity) {
		if (!ENABLED) return

		world.addRigidBody(entity.rigidBody)
		this.objects.add(entity)
	}

	fun dispose() {
		if (!ENABLED) return

		this.collisionConfig.dispose()
		this.dispatcher.dispose()
		this.btSweep3.dispose()
		this.objects.forEach { it.dispose() }
	}

}