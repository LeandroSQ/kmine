package quevedo.soares.leandro.kmine.core.utils

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import quevedo.soares.leandro.kmine.core.enums.CubeTexture

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
