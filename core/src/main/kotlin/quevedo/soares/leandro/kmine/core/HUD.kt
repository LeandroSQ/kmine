package quevedo.soares.leandro.kmine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
import quevedo.soares.leandro.kmine.core.shader.HUDShader
import quevedo.soares.leandro.kmine.core.utils.use

private val FONT_SIZE = 46f * Gdx.graphics.density

class HUD {

	private lateinit var font: BitmapFont
	private lateinit var spriteBatch: SpriteBatch
	private lateinit var camera: OrthographicCamera
	private lateinit var shapeRenderer: ShapeRenderer

	private val width get() = this.camera.viewportWidth
	private val height get() = this.camera.viewportHeight

	fun onCreate() {
		this.createFont()
		this.createCamera()
		this.createSpriteBatch()
		this.createShapeRenderer()
	}

	private fun createFont() {
		val generator = FreeTypeFontGenerator(Gdx.files.local("default.otf"))
		val parameters = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
			magFilter = Texture.TextureFilter.Nearest
			minFilter = Texture.TextureFilter.Nearest
			color = Color.WHITE
			size = FONT_SIZE.toInt()
			shadowOffsetX = 1
			shadowOffsetY = 1
		}

		this.font = generator.generateFont(parameters)
		generator.dispose()
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
		this.shapeRenderer = ShapeRenderer(64, HUDShader.load()).apply {
			color = Color.WHITE
			projectionMatrix = camera.combined
		}
	}

	private fun renderCrosshair() {
		val thickness = 5f
		val size = 12.5f
		val centerX = width / 2f
		val centerY = height / 2f

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

	private fun renderFpsCounter() {
		val padding = 10f
		this.font.draw(this.spriteBatch, "FPS: ${Gdx.graphics.framesPerSecond}", padding, height - padding)
	}

	private fun renderStatistics() {
		val padding = 10f
		Game.world.calculateStatistics().let {
			this.font.draw(this.spriteBatch, "Vertices: ${it.visibleVerticesCount} / ${it.totalVerticesCount}", padding, height - padding - font.lineHeight)
			this.font.draw(this.spriteBatch, "Indices: ${it.visibleIndicesCount} / ${it.totalIndicesCount}", padding, height - padding - font.lineHeight * 2)
			this.font.draw(this.spriteBatch, "Chunks: ${it.visibleChunksCount} / ${it.totalChunksCount}", padding, height - padding - font.lineHeight * 3)
		}

		Game.player.position.let {
			this.font.draw(this.spriteBatch, "Player: { %.2f, %.2f, %.2f }".format(it.x, it.y, it.z), padding, height - padding - font.lineHeight * 4)
		}
	}

	fun render() {
		this.spriteBatch.begin()
		this.renderCrosshair()
		this.renderFpsCounter()
		this.renderStatistics()
		this.spriteBatch.end()
	}

	fun dispose() {
		this.font.dispose()
		this.spriteBatch.dispose()
	}

	fun onResize(width: Float, height: Float) {
		this.camera.viewportWidth = width
		this.camera.viewportHeight = height
		this.camera.position.set(width / 2f, height / 2f, 1f)
		this.camera.update()

		this.spriteBatch.projectionMatrix = this.camera.combined
		this.shapeRenderer.projectionMatrix = this.camera.combined
	}

}