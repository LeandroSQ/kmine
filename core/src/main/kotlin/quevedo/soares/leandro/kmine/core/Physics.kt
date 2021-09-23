package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.MathUtils.floor
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import ktx.math.div
import quevedo.soares.leandro.kmine.core.models.PhysicsProperties
import quevedo.soares.leandro.kmine.core.models.PlayerPhysicsProperties
import quevedo.soares.leandro.kmine.core.models.RayHit
import quevedo.soares.leandro.kmine.core.utils.vec3

private const val SIMULATION_MAX_SUBSTEPS = 5

class Physics {

	private lateinit var collisionConfig: btCollisionConfiguration
	private lateinit var dispatcher: btDispatcher
	private lateinit var sequentialImpulseConstraintSolver: btSequentialImpulseConstraintSolver
	private lateinit var world: btDiscreteDynamicsWorld
	private lateinit var btSweep3: btAxisSweep3
	private lateinit var debugDrawer: DebugDrawer

	private val simulationContext = newSingleThreadAsyncContext("Physics-Thread")

	fun onCreate() {
		this.collisionConfig = btDefaultCollisionConfiguration()
		this.dispatcher = btCollisionDispatcher(this.collisionConfig)
		this.sequentialImpulseConstraintSolver = btSequentialImpulseConstraintSolver()
		this.btSweep3 = btAxisSweep3(Vector3(-1000f, -1000f, -1000f), Vector3(1000f, 1000f, 1000f))
		this.world = btDiscreteDynamicsWorld(dispatcher, btSweep3, sequentialImpulseConstraintSolver, collisionConfig).apply {
			gravity = Vector3(0f, -GRAVITY_FORCE, 0f)
			dispatchInfo.allowedCcdPenetration = 0.0001f
			/*debugDrawer = DebugDrawer().also {
				this@Physics.debugDrawer = it
			}*/
		}
	}

	private fun renderDebug() {
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

		this.debugDrawer.shapeRenderer.projectionMatrix = Game.player.camera.combined
		this.debugDrawer.debugMode = btIDebugDraw.DebugDrawModes.DBG_DrawAabb

		this.debugDrawer.begin(Game.player.camera)
		this.world.debugDrawWorld()
		this.debugDrawer.end()

		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
	}

	fun update() {
		KtxAsync.launch (simulationContext) {
			world.stepSimulation(Gdx.graphics.deltaTime, SIMULATION_MAX_SUBSTEPS)
		}
	}

	fun dispose() {
		this.collisionConfig.dispose()
		this.dispatcher.dispose()
		this.btSweep3.dispose()
	}

	fun castRay(from: Vector3, to: Vector3): RayHit? {
		val callback = ClosestRayResultCallback(from, to).apply {
			collisionObject = null
			closestHitFraction = 1f
			collisionFilterGroup = btBroadphaseProxy.CollisionFilterGroups.CharacterFilter
		}

		this.world.rayTest(from, to, callback)

		if (callback.hasHit()) {
			val position = Vector3(0f, 0f, 0f)
			callback.getHitPointWorld(position)

			var normal = Vector3(0f, 0f, 0f)
			callback.getHitNormalWorld(normal)

			return RayHit(
				point = vec3(
					floor(position.x + 0.5f),
					floor(position.y + 0.5f),
					floor(position.z + 0.5f)
				),
				normal = normal / 2f
			)

		}

		return null
	}

	fun remove(properties: PhysicsProperties) {
		world.removeRigidBody(properties.rigidBody)
	}

	fun add(properties: PhysicsProperties) {
		if (properties.isStatic) {
			world.addRigidBody(
				properties.rigidBody,
				btBroadphaseProxy.CollisionFilterGroups.StaticFilter,
				btBroadphaseProxy.CollisionFilterGroups.CharacterFilter or btBroadphaseProxy.CollisionFilterGroups.DefaultFilter
			)
		} else {
			world.addRigidBody(properties.rigidBody)
		}
	}

	fun add(properties: PlayerPhysicsProperties) {
		btSweep3.overlappingPairCache.setInternalGhostPairCallback(btGhostPairCallback())

		properties.controller.gravity = this.world.gravity

		world.addCollisionObject(
			properties.ghostObject,
			btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
			btBroadphaseProxy.CollisionFilterGroups.StaticFilter or btBroadphaseProxy.CollisionFilterGroups.DefaultFilter
		)

		world.addAction(properties.controller)
	}

	companion object {
		const val GRAVITY_FORCE = 9.80665f * 2f
	}

}