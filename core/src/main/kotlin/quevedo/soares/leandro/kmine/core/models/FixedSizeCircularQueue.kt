package quevedo.soares.leandro.kmine.core.models

class FixedSizeCircularQueue(val maxSize: Int) {

	private val items = arrayListOf<Short>()
	var max: Short = 0
		private set

	val size: Int get() = this.items.size

	fun push(item: Short) {
		if (item > max) max = item
		if (this.items.size >= maxSize) {
			this.items.removeFirst()
			if (item < max && max > 20) max --
		}
		this.items.add(item)
	}

	fun forEach(callback: (index: Int, item: Short) -> Unit) {
		for ((index, item) in this.items.withIndex()) {
			callback.invoke(index, item)
		}
	}

}