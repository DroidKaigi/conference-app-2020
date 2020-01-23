package io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper

import com.soywiz.klock.DateFormat
import com.soywiz.klock.parse
import io.github.droidkaigi.confsched2020.data.api.response.CategoryItemResponse
import io.github.droidkaigi.confsched2020.data.api.response.CategoryResponse
import io.github.droidkaigi.confsched2020.data.api.response.RoomResponse
import io.github.droidkaigi.confsched2020.data.api.response.SessionResponse
import io.github.droidkaigi.confsched2020.data.api.response.SpeakerResponse
import io.github.droidkaigi.confsched2020.data.db.internal.entity.CategoryEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.MessageEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.RoomEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SessionEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SessionFeedbackEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SessionSpeakerJoinEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SpeakerEntityImpl
import io.github.droidkaigi.confsched2020.model.SessionFeedback

internal fun List<SessionResponse>?.toSessionSpeakerJoinEntities():
    List<SessionSpeakerJoinEntityImpl> {
    val sessionSpeakerJoinEntity: MutableList<SessionSpeakerJoinEntityImpl> = arrayListOf()
    this?.forEach { responseSession ->
        responseSession.speakers.forEach { speakerId ->
            sessionSpeakerJoinEntity +=
                SessionSpeakerJoinEntityImpl(responseSession.id, speakerId)
        }
    }
    return sessionSpeakerJoinEntity
}

internal fun List<SessionResponse>.toSessionEntities(
    categories: List<CategoryResponse>,
    rooms: List<RoomResponse>
): List<SessionEntityImpl> =
    this.map {
        it.toSessionEntityImpl(categories, rooms)
    }

private val dateFormat: DateFormat =
    DateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

internal fun SessionResponse.toSessionEntityImpl(
    categories: List<CategoryResponse>,
    rooms: List<RoomResponse>
): SessionEntityImpl {
    val room = rooms.room(roomId)
    if (!isServiceSession) {
        val category = categories.category(2, sessionCategoryItemId)
        return SessionEntityImpl(
            id = id,
            isServiceSession = isServiceSession,
            title = requireNotNull(title?.ja),
            enTitle = requireNotNull(title?.en),
            desc = description ?: "",
            stime = dateFormat.parse(requireNotNull(startsAt)).utc.unixMillisLong,
            etime = dateFormat.parse(requireNotNull(endsAt)).utc.unixMillisLong,
            language = language ?: "JAPANESE",
            message = message?.let {
                MessageEntityImpl(requireNotNull(it.ja), requireNotNull(it.en))
            },
            category = CategoryEntityImpl(
                requireNotNull(category.id),
                requireNotNull(category.name?.ja),
                requireNotNull(category.name?.en)
            ),
            intendedAudience = targetAudience,
            videoUrl = videoUrl,
            slideUrl = slideUrl,
            isInterpretationTarget = interpretationTarget,
            room = RoomEntityImpl(
                requireNotNull(room.id),
                requireNotNull(room.name?.ja),
                requireNotNull(room.name?.en),
                requireNotNull(room.sort)
            ),
            sessionType = sessionType
        )
    } else {
        return SessionEntityImpl(
            id = id,
            isServiceSession = isServiceSession,
            enTitle = requireNotNull(title?.en),
            title = requireNotNull(title?.ja),
            desc = description ?: "",
            stime = dateFormat.parse(requireNotNull(startsAt)).utc.unixMillisLong,
            etime = dateFormat.parse(requireNotNull(endsAt)).utc.unixMillisLong,
            language = requireNotNull(language),
            category = null,
            room = RoomEntityImpl(
                requireNotNull(room.id),
                requireNotNull(room.name?.ja),
                requireNotNull(room.name?.en),
                requireNotNull(room.sort)
            ),
            intendedAudience = null,
            videoUrl = videoUrl,
            slideUrl = slideUrl,
            isInterpretationTarget = interpretationTarget,
            message = message?.let {
                MessageEntityImpl(requireNotNull(it.ja), requireNotNull(it.en))
            },
            sessionType = sessionType
        )
    }
}

internal fun List<SpeakerResponse>.toSpeakerEntities(): List<SpeakerEntityImpl> =
    map { responseSpeaker ->
        SpeakerEntityImpl(
            id = responseSpeaker.id!!,
            name = responseSpeaker.fullName!!,
            tagLine = responseSpeaker.tagLine,
            bio = responseSpeaker.bio,
            imageUrl = responseSpeaker.profilePicture
        )
    }

private fun List<CategoryResponse>.category(
    categoryIndex: Int,
    categoryId: Int?
): CategoryItemResponse {
    return this[categoryIndex].items!!.first { it!!.id == categoryId }!!
}

private fun List<RoomResponse>.room(roomId: Int?): RoomResponse = first { it.id == roomId }

internal fun SessionFeedback.toSessionFeedbackEntity(): SessionFeedbackEntityImpl =
    SessionFeedbackEntityImpl(
        sessionId = sessionId,
        totalEvaluation = totalEvaluation,
        relevancy = relevancy,
        asExpected = asExpected,
        difficulty = difficulty,
        knowledgeable = knowledgeable,
        comment = comment,
        submitted = submitted
    )
