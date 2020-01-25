package io.github.droidkaigi.confsched2020.model

import kotlinx.android.parcel.IgnoredOnParcel

@AndroidParcelize
open class SessionPage : AndroidParcel {

    @AndroidParcelize
    object Event : SessionPage() {
        override val title = "Event"
    }

    @AndroidParcelize
    object Favorite : SessionPage() {
        override val title = "My Plan"
    }

    @AndroidParcelize
    open class Day(
        override val title: String,
        val day: Int
    ) : SessionPage()

    @AndroidParcelize
    object Day1 : Day("Day 1", 1)

    @AndroidParcelize
    object Day2 : Day("Day 2", 2)

    @IgnoredOnParcel
    open val title: String = ""

    companion object {
        val pages = listOf(Day1, Day2, Event, Favorite)

        fun dayOfNumber(dayNumber: Int): Day {
            return pages.filterIsInstance<Day>().first { it.day == dayNumber }
        }
    }
}
