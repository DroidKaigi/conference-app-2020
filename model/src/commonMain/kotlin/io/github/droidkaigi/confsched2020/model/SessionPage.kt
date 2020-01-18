package io.github.droidkaigi.confsched2020.model

sealed class SessionPage {

    object Favorite : SessionPage() {
        override val title = "My Plan"
    }

    open class Day(
        override val title: String,
        val day: Int
    ) : SessionPage()

    object Day1 : Day("Day 1", 1)
    object Day2 : Day("Day 2", 2)

    abstract val title: String

    companion object {
        val pages = listOf(Day1, Day2, Favorite)

        fun dayOfNumber(dayNumber: Int): Day {
            return pages.filterIsInstance<Day>().first { it.day == dayNumber }
        }
    }
}
