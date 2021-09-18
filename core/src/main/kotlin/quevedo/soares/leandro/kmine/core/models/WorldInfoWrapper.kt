package quevedo.soares.leandro.kmine.core.models

data class WorldInfoWrapper (
	val totalVerticesCount: Int,
	val visibleVerticesCount: Int,
	val totalIndicesCount: Int,
	val visibleIndicesCount: Int,
	val totalChunksCount: Int,
	val visibleChunksCount: Int
)