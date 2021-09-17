package quevedo.soares.leandro.kmine.core.terrain.type

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.enums.CubeTexture
import quevedo.soares.leandro.kmine.core.models.CubeFaceTextureMap
import quevedo.soares.leandro.kmine.core.terrain.Cube

private const val SIZE = 0.075f
private const val HEIGHT = 0.30f
private const val GROUND_OFFSET = HEIGHT - 0.5f

class TorchCube : Cube {

	override var textureMap = CubeFaceTextureMap(
		top = CubeTexture.TORCH_TOP,
		bottom = CubeTexture.TORCH_TOP,
		sides = CubeTexture.TORCH
	)
	override var isTranslucent = true

	override val frontFace
		get() = floatArrayOf(
			-SIZE, -HEIGHT + GROUND_OFFSET, SIZE,
			SIZE, -HEIGHT + GROUND_OFFSET, SIZE,
			SIZE, HEIGHT, SIZE,
			-SIZE, HEIGHT, SIZE
		)
	override val backFace
		get() = floatArrayOf(
			SIZE, -HEIGHT + GROUND_OFFSET, -SIZE,
			-SIZE, -HEIGHT + GROUND_OFFSET, -SIZE,
			-SIZE, HEIGHT, -SIZE,
			SIZE, HEIGHT, -SIZE
		)
	override val topFace
		get() = floatArrayOf(
			-SIZE, HEIGHT, -SIZE,
			-SIZE, HEIGHT, SIZE,
			SIZE, HEIGHT, SIZE,
			SIZE, HEIGHT, -SIZE
		)
	override val leftFace
		get() = floatArrayOf(
			-SIZE, -HEIGHT + GROUND_OFFSET, -SIZE,
			-SIZE, -HEIGHT + GROUND_OFFSET, SIZE,
			-SIZE, HEIGHT, SIZE,
			-SIZE, HEIGHT, -SIZE
		)
	override val rightFace
		get() = floatArrayOf(
			SIZE, -HEIGHT + GROUND_OFFSET, SIZE,
			SIZE, -HEIGHT + GROUND_OFFSET, -SIZE,
			SIZE, HEIGHT, -SIZE,
			SIZE, HEIGHT, SIZE
		)
	override val bottomFace
		get() = floatArrayOf(
			-SIZE, -HEIGHT + GROUND_OFFSET, -SIZE,
			SIZE, -HEIGHT + GROUND_OFFSET, -SIZE,
			SIZE, -HEIGHT + GROUND_OFFSET, SIZE,
			-SIZE, -HEIGHT + GROUND_OFFSET, SIZE
		)

	constructor() : super()
	constructor(position: Vector3) : super(position)

}