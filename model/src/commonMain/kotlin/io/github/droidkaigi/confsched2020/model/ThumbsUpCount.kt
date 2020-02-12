package io.github.droidkaigi.confsched2020.model

data class ThumbsUpCount(
    val total: Int,
    val incremented: Int,
    val incrementedUpdated: Boolean
) {
    companion object {
        val ZERO = ThumbsUpCount(
            total = 0,
            incremented = 0,
            incrementedUpdated = false
        )
    }
}
