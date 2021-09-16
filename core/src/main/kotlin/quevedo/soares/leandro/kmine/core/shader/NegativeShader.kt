package quevedo.soares.leandro.kmine.core.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram

object NegativeShader {

	fun load(): ShaderProgram {
		ShaderProgram.pedantic = false

		val shader = ShaderProgram(
			Gdx.files.local("/shaders/negative.vert"),
			Gdx.files.local("/shaders/negative.frag")
		)

		if (!shader.isCompiled) throw Exception(shader.log)

		return shader
	}

}