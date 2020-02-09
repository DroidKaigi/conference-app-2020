package io.github.droidkaigi.confsched2020.data.repository

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.parse
import io.github.droidkaigi.confsched2020.data.api.response.CategoryItemResponse
import io.github.droidkaigi.confsched2020.data.api.response.CategoryResponse
import io.github.droidkaigi.confsched2020.data.api.response.Response
import io.github.droidkaigi.confsched2020.data.api.response.RoomResponse
import io.github.droidkaigi.confsched2020.data.api.response.SessionResponse
import io.github.droidkaigi.confsched2020.data.api.response.SpeakerResponse
import io.github.droidkaigi.confsched2020.model.AudienceCategory
import io.github.droidkaigi.confsched2020.model.Category
import io.github.droidkaigi.confsched2020.model.Lang
import io.github.droidkaigi.confsched2020.model.LangSupport
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

private val dateFormat: DateFormat =
    DateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

fun Response.toModel(): SessionContents {
    val firstDay = dateFormat.parse(requireNotNull(sessions.first().startsAt))

    val sessions = SessionList(sessions.map {
        it.toSession(
            firstDay,
            requireNotNull(speakers),
            requireNotNull(rooms),
            requireNotNull(categories)
        )
    })
    val speechSessions = sessions.filterIsInstance<SpeechSession>()
    return SessionContents(
        sessions = sessions,
        speakers = speechSessions.flatMap { it.speakers }.distinct(),
        langs = Lang.values().toList(),
        langSupports = LangSupport.values().toList(),
        rooms = sessions.map { it.room }.sortedBy { it.sort }.distinct(),
        category = speechSessions.map { it.category }.distinct(),
        audienceCategories = AudienceCategory.values().toList()
    )
}

private fun SessionResponse.toSession(
    firstDay: DateTimeTz,
    speakers: List<SpeakerResponse>,
    rooms: List<RoomResponse>,
    categoryResponse: List<CategoryResponse>
): Session {
    val response = this
    val startTime = dateFormat.parse(requireNotNull(response.startsAt))
    val endTime = dateFormat.parse(requireNotNull(response.endsAt))
    val room = rooms.first { response.roomId == it.id }

    return if (!response.isServiceSession) {
        val category = categoryResponse.category(2, sessionCategoryItemId)
        SpeechSession(
            id = SessionId(response.id),
            // dayNumber is starts with 1.
            // Example: First day = 1, Second day = 2. So I plus 1 to period days
            dayNumber = startTime.dayOfYear - firstDay.dayOfYear + 1,
            startTime = startTime.utc,
            endTime = endTime.utc,
            title = LocaledString(
                requireNotNull(response.title?.ja),
                requireNotNull(response.title?.en)
            ),
            desc = response.description ?: "",
            room = Room(
                requireNotNull(room.id),
                LocaledString(requireNotNull(room.name?.ja), requireNotNull(room.name?.en)),
                requireNotNull(room.sort)
            ),
            lang = Lang.findLang(requireNotNull(language)),
            category = Category(
                requireNotNull(category.id),
                LocaledString(
                    ja = requireNotNull(category.name?.ja),
                    en = requireNotNull(category.name?.en)
                )
            ),
            intendedAudience = targetAudience,
            videoUrl = videoUrl,
            slideUrl = slideUrl,
            isInterpretationTarget = interpretationTarget,
            // TODO
            isFavorited = false,
            speakers = response.speakers
                .map { speakerId -> speakers.first { speakerId == it.id } }
                .map {
                    Speaker(
                        id = SpeakerId(requireNotNull(it.id)),
                        name = requireNotNull(it.fullName),
                        bio = it.bio,
                        tagLine = it.tagLine,
                        imageUrl = it.profilePicture
                    )
                },
            message = response.message?.let {
                LocaledString(
                    requireNotNull(it.ja),
                    requireNotNull(it.en)
                )
            }
        )
    } else {
        ServiceSession(
            id = SessionId(response.id),
            // dayNumber is starts with 1.
            // Example: First day = 1, Second day = 2. So I plus 1 to period days
            dayNumber = startTime.dayOfYear - firstDay.dayOfYear + 1,
            startTime = startTime.utc,
            endTime = endTime.utc,
            title = LocaledString(
                requireNotNull(response.title?.ja),
                requireNotNull(response.title?.en)
            ),
            desc = response.description ?: "",
            room = Room(
                requireNotNull(room.id),
                LocaledString(requireNotNull(room.name?.ja), requireNotNull(room.name?.en)),
                requireNotNull(room.sort)
            ),
            // TODO
            isFavorited = false,
            sessionType = SessionType.of(response.sessionType)
        )
    }
}

private fun List<CategoryResponse>.category(
    categoryIndex: Int,
    categoryId: Int?
): CategoryItemResponse {
    return this[categoryIndex].items!!.first { it!!.id == categoryId }!!
}
