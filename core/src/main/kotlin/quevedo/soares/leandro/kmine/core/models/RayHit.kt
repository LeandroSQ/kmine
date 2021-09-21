package quevedo.soares.leandro.kmine.core.models

import com.badlogic.gdx.math.Vector3
import ktx.math.minus
import ktx.math.plus
import quevedo.soares.leandro.kmine.core.utils.floor

data class RayHit(
	val point: Vector3,
	val normal: Vector3
) {
	val negative get() = (this.point - this.normal).floor()
	val positive get() = (this.point + this.normal).floor()
}