package quevedo.soares.leandro.kmine.core.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram

object MeshShader {

	fun load(): ShaderProgram {
		ShaderProgram.pedantic = false

		val shader = ShaderProgram(
			Gdx.files.local("/shaders/mesh_vertex.glsl"),
			Gdx.files.local("/shaders/mesh_fragment.glsl")
		)

		if (!shader.isCompiled) throw Exception(shader.log)

		return shader
	}

}