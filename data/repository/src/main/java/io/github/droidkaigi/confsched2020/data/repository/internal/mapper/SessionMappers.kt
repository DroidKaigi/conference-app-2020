package io.github.droidkaigi.confsched2020.data.repository.internal.mapper

import com.soywiz.klock.DateTime
import com.soywiz.klock.hours
import io.github.droidkaigi.confsched2020.data.db.entity.SessionFeedbackEntity
import io.github.droidkaigi.confsched2020.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2020.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2020.model.Category
import io.github.droidkaigi.confsched2020.model.Lang
import io.github.droidkaigi.confsched2020.model.LocaledString
import io.github.droidkaigi.confsched2020.model.Room
import io.github.droidkaigi.confsched2020.model.ServiceSession
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionFeedback
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.SessionType
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.model.SpeakerId
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.defaultTimeZoneOffset

fun SessionWithSpeakers.toSession(
    speakerEntities: List<SpeakerEntity>,
    favList: List<String>?,
    firstDay: DateTime
): Session {
    return if (session.isServiceSession) {
        ServiceSession(
            id = SessionId(session.id),
            // dayNumber is starts with 1.
            // Example: First day = 1, Second day = 2. So I plus 1 to period days
            dayNumber = DateTime(session.stime).toOffset(defaultTimeZoneOffset()).dayOfYear -
                firstDay.toOffset(defaultTimeZoneOffset()).dayOfYear + 1,
            startTime = DateTime.fromUnix(session.stime),
            endTime = DateTime.fromUnix(session.etime),
            title = LocaledString(
                ja = session.title,
                en = requireNotNull(session.enTitle)
            ),
            desc = session.desc,
            room = requireNotNull(session.room).let { room ->
                Room(room.id, LocaledString(room.name, room.enName), room.sort)
            },
            sessionType = SessionType.of(session.sessionType),
            isFavorited = favList!!.contains(session.id)
        )
    } else {
        require(speakerIdList.isNotEmpty())
        val speakers = speakerIdList.map { speakerId ->
            val speakerEntity = speakerEntities.first { it.id == speakerId }
            speakerEntity.toSpeaker()
        }
        require(speakers.isNotEmpty())
        SpeechSession(
            id = SessionId(session.id),
            // dayNumber is starts with 1.
            // Example: First day = 1, Second day = 2. So I plus 1 to period days
            dayNumber = DateTime(session.stime).toOffset(defaultTimeZoneOffset()).dayOfYear -
                firstDay.toOffset(defaultTimeZoneOffset()).dayOfYear + 1,
            startTime = DateTime.fromUnix(session.stime),
            endTime = DateTime.fromUnix(session.etime),
            title = LocaledString(session.title, requireNotNull(session.enTitle)),
            desc = session.desc,
            room = requireNotNull(session.room).let {
                Room(it.id, LocaledString(it.name, it.enName), it.sort)
            },
            lang = Lang.findLang(session.language),
            category = requireNotNull(session.category).let { category ->
                Category(
                    category.id,
                    LocaledString(
                        ja = category.name,
                        en = category.enName
                    )
                )
            },
            intendedAudience = session.intendedAudience,
            videoUrl = session.videoUrl,
            slideUrl = session.slideUrl,
            isInterpretationTarget = session.isInterpretationTarget,
            isFavorited = favList!!.contains(session.id),
            speakers = speakers,
            message = session.message?.let {
                LocaledString(it.ja, it.en)
            }
        )
    }
}

fun SpeakerEntity.toSpeaker(): Speaker = Speaker(
    id = SpeakerId(id),
    name = name,
    tagLine = tagLine,
    bio = bio,
    imageUrl = imageUrl
)

fun SessionFeedbackEntity.toSessionFeedback(): SessionFeedback =
    SessionFeedback(
        sessionId = sessionId,
        totalEvaluation = totalEvaluation,
        relevancy = relevancy,
        asExpected = asExpected,
        difficulty = difficulty,
        knowledgeable = knowledgeable,
        comment = comment,
        submitted = submitted
    )
