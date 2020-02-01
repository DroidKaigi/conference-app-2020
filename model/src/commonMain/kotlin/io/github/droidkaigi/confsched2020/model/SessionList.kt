package io.github.droidkaigi.confsched2020.model

data class SessionList(private val sessions: List<Session>) : List<Session> by sessions {
    val dayToSessionMap: Map<SessionPage.Day, SessionList> by lazy {
        minus(events)
            .groupBy { it.dayNumber }
            .mapKeys {
                SessionPage.dayOfNumber(
                    it.key
                )
            }
            .mapValues { (_, value) -> SessionList(value) }
    }

    val events: SessionList by lazy {
        SessionList(filter { it.room.roomType == Room.RoomType.EXHIBITION })
    }

    val favorited: SessionList by lazy {
        SessionList(filter { it.isFavorited })
    }

    val currentSessionIndex by lazy {
        when (val lastFinished = indexOfLast { it.isFinished }) {
            -1 -> {
                // if sessions dose not started we don't use it
                -1
            }
            size - 1 -> {
                // if it is last we don't use it
                -1
            }
            else -> {
                lastFinished + 1
            }
        }
    }

    fun filtered(filters: Filters): SessionList {
        return SessionList(filter { filters.isPass(it) })
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
