package quevedo.soares.leandro.kmine.core.utils

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.enums.CubeTexture
import kotlin.math.sqrt

operator fun FloatArray.times(size: Float) = this.map { it * size }.toFloatArray()

inline var Matrix4.position: Vector3
	get() {
		val v = Vector3()
		this.getTranslation(v)
		return v
	}
	set(value) {
		this.setTranslation(value)
	}

fun ModelBuilder.createQuad(size: Float, material: Material, attributes: Long): Model {
	val v = floatArrayOf(
		-0.5f, -0.5f, 0f,
		0.5f, -0.5f, 0f,
		0.5f, 0.5f, 0f,
		-0.5f, 0.5f, 0f
	).map { it * size }

	val n = floatArrayOf(0f, 0f, 1f)

	return this.createRect(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], n[0], n[1], n[2], GL20.GL_TRIANGLES, material, attributes)
}

fun ModelBatch.use(camera: Camera, callback: () -> Unit) {
	this.begin(camera)
	callback.invoke()
	this.end()
}

fun clamp(x: Int, min: Int, max: Int) = if (x > max) max else if (x < min) min else x

fun ShapeRenderer.use(shapeType: ShapeRenderer.ShapeType = ShapeRenderer.ShapeType.Filled, callback: () -> Unit) {
	this.begin(shapeType)
	callback.invoke()
	this.end()
}

fun vec2(x: Int = 0, y: Int = 0) = Vector2(x.toFloat(), y.toFloat())
inline val Vector2.xInt get() = this.x.toInt()
inline val Vector2.yInt get() = this.y.toInt()
fun Vector2.dist(other: Vector2): Float {
	val x = other.x - this.x
	val y = other.y - this.y
	return sqrt(x * x + y * y)
}

fun vec3(x: Int = 0, y: Int = 0, z: Int = 0) = Vector3(x.toFloat(), y.toFloat(), z.toFloat())
fun Vector3.dist(other: Vector3): Float {
	val x = other.x - this.x
	val y = other.y - this.y
	val z = other.z - this.z
	return sqrt(x * x + y * y + z * z)
}

inline val Vector3.xInt get() = this.x.toInt()
inline val Vector3.yInt get() = this.y.toInt()
inline val Vector3.zInt get() = this.z.toInt()

fun Vector3.toFloatArray() = floatArrayOf(this.x, this.y, this.z)

fun FloatArray.add(other: FloatArray): FloatArray {
	this.clone().apply {
		for (i in 0 until this.size) {
			val j = i % other.size
			this[i] = this[i] + other[j]
		}

		return this
	}
}

fun MeshPartBuilder.addQuad(vertices: FloatArray, normals: FloatArray, origin: FloatArray, textureAtlas: TextureAtlas, textureRegion: CubeTexture): Int {
	val v = vertices.add(origin)
	this.setUVRange(textureAtlas.findRegion(textureRegion.regionName))
	this.rect(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], normals[0], normals[1], normals[2])
	return vertices.size
}
