package io.github.droidkaigi.confsched2020.model

data class SessionList(private val sessions: List<Session>) : List<Session> by sessions {
    val dayToSessionMap: Map<SessionPage.Day, SessionList> by lazy {
        sessions
            .groupBy { it.dayNumber }
            .mapKeys {
                SessionPage.dayOfNumber(
                    it.key
                )
            }
            .mapValues { (_, value) -> SessionList(value) }
    }

    val favorited: SessionList by lazy {
        SessionList(sessions.filter { it.isFavorited })
    }

    val currentSessionIndex by lazy {
        val lastFinished = sessions.indexOfLast { it.isFinished }
        if (size - 1 == lastFinished) {
            lastFinished
        } else {
            lastFinished + 1
        }
    }

    fun filtered(filters: Filters): SessionList {
        return SessionList(sessions
            .filter { filters.isPass(it) })
    }

    fun toPageToScrollPositionMap(): Map<SessionPage, Int> {
        val map = mutableMapOf<SessionPage, Int>()
        map += dayToSessionMap
            .map { (day, sessions) ->
                val index = sessions.currentSessionIndex
                day to index
            }
            .filter { (_, value) -> value != -1 }
            .toMap()
        val favoriteCurrentSessionIndex = favorited.currentSessionIndex
        if (favoriteCurrentSessionIndex != -1) {
            map += SessionPage.Favorite to favoriteCurrentSessionIndex
        }
        return map
    }

    companion object {
        val EMPTY = SessionList(listOf())
    }
}
