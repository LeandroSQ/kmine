package quevedo.soares.leandro.kmine.core.utils

import com.badlogic.gdx.math.Vector3
import quevedo.soares.leandro.kmine.core.terrain.Cube

class CubeMatrix : ArrayList<ArrayList<ArrayList<Cube?>>> {

	constructor(initialCapacity: Int) : super(initialCapacity)
	constructor() : super()
	constructor(c: MutableCollection<out java.util.ArrayList<java.util.ArrayList<Cube?>>>) : super(c)
	constructor(width: Int, height: Int, depth: Int, default: Cube? = null) {
		// Add cubes in the x dimension
		for (x in 0 until width) {
			// Add cubes in the y dimension
			val bufferY = arrayListOf<ArrayList<Cube?>>()
			for (y in 0 until height) {
				// Add cubes in the z dimension
				val bufferZ = arrayListOf<Cube?>()
				for (z in 0 until depth) {
					bufferZ.add(default)
				}
				bufferY.add(bufferZ)
			}
			this.add(bufferY)
		}
	}

	val width get() = this.size
	val height get() = this[0].size
	val depth get() = this[0][0].size

	fun set(x: Int, y: Int, z: Int, cube: Cube?) {
		this[x][y][z] = cube
	}

	fun get(x: Int, y: Int, z: Int): Cube? {
		return this[x][y][z]
	}

	fun get(position: Vector3): Cube? {
		return this[position.xInt][position.yInt][position.zInt]
	}

	inline fun forEach(callback: (x: Int, y: Int, z: Int, cube: Cube?) -> Unit) {
		for (x in 0 until this.width) {
			for (y in 0 until this.height) {
				for (z in 0 until this.depth) {
					callback.invoke(x, y, z, this.get(x, y, z))
				}
			}
		}
	}

}