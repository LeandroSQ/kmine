package quevedo.soares.leandro.kmine.core.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.GdxRuntimeException
import quevedo.soares.leandro.kmine.core.utils.position

class MeshShader(private val renderable: Renderable) : BaseShader() {

	private val u_projTrans = register(Uniform("u_projTrans"))

	init {
		renderable.shader = this

		init()
	}

	override fun init() {
		ShaderProgram.pedantic = true

		val shader = ShaderProgram(
			Gdx.files.local("/shaders/mesh_vertex.glsl"),
			Gdx.files.local("/shaders/mesh_fragment.glsl")
		)

		if (!shader.isCompiled) throw Exception(shader.log)

		this.init(shader, this.renderable)
	}

	override fun render(renderable: Renderable?) {
		renderable?.let {
			set(u_projTrans, this.camera.projection)

		}

		super.render(renderable)
	}

	override fun compareTo(other: Shader?) = 0

	override fun canRender(instance: Renderable?) = true

	override fun dispose() {
		this.program.dispose()
		super.dispose()
	}

}