package io.github.droidkaigi.confsched2020.model

class SearchResult(
    val sessions: List<Session>,
    val speakers: List<Speaker>,
    val query: String?
) {
    fun isEmpty() = this == EMPTY

    companion object {
        val EMPTY = SearchResult(listOf(), listOf(), null)
    }
}
