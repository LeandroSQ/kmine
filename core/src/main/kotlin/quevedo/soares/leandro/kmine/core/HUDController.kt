package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.math.vec2
import quevedo.soares.leandro.kmine.core.shader.NegativeShader
import quevedo.soares.leandro.kmine.core.utils.use

class HUDController {

	private lateinit var font: BitmapFont
	private lateinit var spriteBatch: SpriteBatch
	private lateinit var camera: OrthographicCamera
	private lateinit var shapeRenderer: ShapeRenderer

	fun create() {
		this.createFont()
		this.createCamera()
		this.createSpriteBatch()
		this.createShapeRenderer()
	}

	private fun createFont() {
		this.font = BitmapFont(Gdx.files.local("default.fnt"))
		this.font.color = Color.WHITE
	}

	private fun createCamera() {
		this.camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()).apply {
			position.set(viewportWidth / 2f, viewportHeight / 2f, 1f)
		}
		this.camera.update()
	}

	private fun createSpriteBatch() {
		this.spriteBatch = SpriteBatch().apply {
			projectionMatrix = camera.combined
		}
	}

	private fun createShapeRenderer() {
		this.shapeRenderer = ShapeRenderer(64, NegativeShader.load()).apply {
			color = Color.WHITE
			projectionMatrix = camera.combined
		}
	}

	private fun renderCrosshair() {
		val thickness = 5f
		val size = 10f
		val centerX = Gdx.graphics.width / 2f
		val centerY = Gdx.graphics.height / 2f

		this.shapeRenderer.use {
			// Horizontal line
			this.shapeRenderer.rectLine(
				vec2(centerX - size, centerY),
				vec2(centerX + size, centerY),
				thickness
			)

			// It's important to separate the vertical lines because the blending would result into a void square at their intersection with the horizontal line
			// Vertical bottom line
			this.shapeRenderer.rectLine(
				vec2(centerX, centerY - size),
				vec2(centerX, centerY - thickness / 2f),
				thickness
			)

			// Vertical top line
			this.shapeRenderer.rectLine(
				vec2(centerX, centerY + thickness / 2f),
				vec2(centerX, centerY + size),
				thickness
			)
		}
	}

	fun render() {
		this.spriteBatch.begin()
		this.renderCrosshair()
//		this.font.draw(this.spriteBatch, "+", Gdx.graphics.width / 2f, Gdx.graphics.height / 2f + this.font.lineHeight / 2f)
		this.font.draw(this.spriteBatch, "FPS: ${Gdx.graphics.framesPerSecond}", 0f, this.font.lineHeight)
		this.spriteBatch.end()
	}

	fun dispose() {
		this.font.dispose()
		this.spriteBatch.dispose()
	}

}