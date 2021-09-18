package quevedo.soares.leandro.kmine.core.utils

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.enums.CubeTexture

fun ShapeRenderer.use(shapeType: ShapeRenderer.ShapeType = ShapeRenderer.ShapeType.Filled, callback: () -> Unit) {
	this.begin(shapeType)
	callback.invoke()
	this.end()
}

fun vec2(x: Int = 0, y: Int = 0) = Vector2(x.toFloat(), y.toFloat())
inline val Vector2.xInt get() = this.x.toInt()
inline val Vector2.yInt get() = this.y.toInt()

fun vec3(x: Int = 0, y: Int = 0, z: Int = 0) = Vector3(x.toFloat(), y.toFloat(), z.toFloat())
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

fun MeshPartBuilder.addQuad(vertices: FloatArray, normals: FloatArray, origin: FloatArray, textureAtlas: TextureAtlas, textureRegion: CubeTexture) {
	val v = vertices.add(origin)
	this.setUVRange(textureAtlas.findRegion(textureRegion.regionName))
	this.rect(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], normals[0], normals[1], normals[2])
}
