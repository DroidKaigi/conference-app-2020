package io.github.droidkaigi.confsched2020.model

import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.TimezoneOffset

sealed class Session(
    open val id: SessionId,
    open val title: LocaledString,
    open val desc: String,
    open val dayNumber: Int,
    open val startTime: DateTime,
    open val endTime: DateTime,
    open val room: Room,
    open val isFavorited: Boolean
) {
    val startDayText by lazy { startTime.toOffset(defaultTimeZoneOffset()).format("yyyy.M.d") }

    val startTimeText by lazy { startTime.toOffset(defaultTimeZoneOffset()).format("HH:mm") }

    fun timeSummary(lang: Lang, timezoneOffset: TimezoneOffset) = buildString {
        val startTimeTZ = startTime.toOffset(timezoneOffset)
        val endTimeTZ = endTime.toOffset(timezoneOffset)

        // ex: 2月2日 10:20-10:40
        if (lang == Lang.EN) {
            append(startTimeTZ.format("M"))
            append(".")
            append(startTimeTZ.format("d"))
        } else {
            append(startTimeTZ.format("M"))
            append("月")
            append(startTimeTZ.format("d"))
            append("日")
        }
        append(" ")
        append(startTimeTZ.format("HH:mm"))
        append(" - ")
        append(endTimeTZ.format("HH:mm"))
    }

    // See https://github.com/DroidKaigi/conference-app-2020/issues/419
    fun timeSummary(lang: Lang, timezoneOffset: Double) = timeSummary(lang, TimezoneOffset(timezoneOffset))

    fun summary(lang: Lang, timezoneOffset: TimezoneOffset) = buildString {
        append(timeSummary(lang, timezoneOffset))
        append(" / ")
        append(shortSummary(lang))
    }

    abstract fun shortSummary(lang: Lang): String

    val isFinished: Boolean
        get() = DateTime.nowUnixLong() > endTime.unixMillisLong

    val isOnGoing: Boolean
        get() = DateTime.nowUnixLong() in startTime.unixMillisLong..endTime.unixMillisLong

    val timeInMinutes: Int
        get() = TimeSpan(endTime.unixMillis - startTime.unixMillis).minutes.toInt()

    val hasIntendedAudience: Boolean get() = this is SpeechSession
    val hasSpeaker: Boolean get() = this is SpeechSession
    val shouldCountForFilter: Boolean get() = this is SpeechSession
}

@AndroidParcelize
data class SpeechSession(
    override val id: SessionId,
    override val dayNumber: Int,
    override val startTime: DateTime,
    override val endTime: DateTime,
    override val title: LocaledString,
    override val desc: String,
    override val room: Room,
    val lang: Lang,
    val category: Category,
    val intendedAudience: String?,
    val videoUrl: String?,
    val slideUrl: String?,
    val isInterpretationTarget: Boolean,
    override val isFavorited: Boolean,
    val speakers: List<Speaker>,
    val message: LocaledString?
) : Session(id, title, desc, dayNumber, startTime, endTime, room, isFavorited), AndroidParcel {

    override fun shortSummary(lang: Lang) = buildString {
        append(timeInMinutes)
        append("min")
        append(" / ")
        append(room.name.getByLang(lang))
    }

    val hasVideo: Boolean = videoUrl.isNullOrEmpty().not()
    val hasSlide: Boolean = slideUrl.isNullOrEmpty().not()
}

@AndroidParcelize
data class ServiceSession(
    override val id: SessionId,
    override val dayNumber: Int,
    override val startTime: DateTime,
    override val endTime: DateTime,
    override val title: LocaledString,
    override val desc: String,
    override val room: Room,
    val sessionType: SessionType,
    override val isFavorited: Boolean
) : Session(id, title, desc, dayNumber, startTime, endTime, room, isFavorited), AndroidParcel {

    override fun shortSummary(lang: Lang) = buildString {
        append(timeInMinutes)
        append("min")
        if (sessionType.shouldShowRoom) {
            append(" / ")
            append(room.name.getByLang(lang))
        }
    }
}
