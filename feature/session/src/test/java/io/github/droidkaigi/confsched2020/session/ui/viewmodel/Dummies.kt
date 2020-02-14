package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2020.model.Category
import io.github.droidkaigi.confsched2020.model.Lang
import io.github.droidkaigi.confsched2020.model.LangSupport
import io.github.droidkaigi.confsched2020.model.Level
import io.github.droidkaigi.confsched2020.model.LocaledString
import io.github.droidkaigi.confsched2020.model.Room
import io.github.droidkaigi.confsched2020.model.ServiceSession
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.SessionList
import io.github.droidkaigi.confsched2020.model.SessionType
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.model.SpeakerId
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.ThumbsUpCount

object Dummies {
    val hall = Room(1, LocaledString("JA App bar", "EN App bar"), 1)
    val speakers = listOf(
        Speaker(
            id = SpeakerId("speaker_id"),
            name = "speaker",
            tagLine = "tag line",
            bio = null,
            imageUrl = null
        )
    )
    val category = Category(
        id = 0,
        name = LocaledString(ja = "category ja", en = "category en")
    )
    val serviceSession = ServiceSession(
        id = SessionId("service_session_id"),
        dayNumber = 1,
        startTime = DateTime(2020, 2, 10, 10, 10, 10),
        endTime = DateTime(2020, 2, 10, 11, 10, 10),
        title = LocaledString("キーノート", "KeyNote"),
        desc = "Keynote of droidkaigi",
        room = hall,
        levels = listOf(Level.BEGINNER),
        sessionType = SessionType.WELCOME_TALK,
        isFavorited = true
    )

    val speachSession1 = SpeechSession(
        id = SessionId("speech_session_id"),
        dayNumber = 1,
        startTime = DateTime(2020, 2, 10, 10, 10, 10),
        endTime = DateTime(2020, 2, 10, 11, 10, 10),
        title = LocaledString(
            "JA DroidKaigi Conference App",
            "EN How to DroidKaigi Conference App"
        ),
        desc = "droidkaigi app",
        room = hall,
        category = category,
        intendedAudience = null,
        videoUrl = null,
        slideUrl = null,
        isInterpretationTarget = false,
        isFavorited = false,
        speakers = speakers,
        levels = listOf(Level.BEGINNER),
        message = null,
        lang = Lang.JA
    )
    val sessions = listOf<Session>(
        serviceSession,
        speachSession1
    )
    val sessionContents = SessionContents(
        sessions = SessionList(sessions),
        speakers = speakers,
        rooms = listOf(hall),
        langs = listOf(Lang.JA, Lang.EN),
        langSupports = listOf(LangSupport.INTERPRETATION),
        category = listOf(category),
        levels = listOf(Level.BEGINNER, Level.INTERMEDIATE, Level.ADVANCED)
    )

    val thumbsUpCount = ThumbsUpCount(
        total = 10,
        incremented = 0,
        incrementedUpdated = false
    )
}
