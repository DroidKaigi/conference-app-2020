package io.github.droidkaigi.confsched2020.data.repository.internal

import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.GoogleFormApi
import io.github.droidkaigi.confsched2020.data.db.SessionDatabase
import io.github.droidkaigi.confsched2020.data.firestore.Firestore
import io.github.droidkaigi.confsched2020.data.repository.internal.mapper.toSession
import io.github.droidkaigi.confsched2020.data.repository.internal.mapper.toSessionFeedback
import io.github.droidkaigi.confsched2020.data.repository.internal.workmanager.FavoriteToggleWork
import io.github.droidkaigi.confsched2020.model.Lang
import io.github.droidkaigi.confsched2020.model.LangSupport
import io.github.droidkaigi.confsched2020.model.Level
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionFeedback
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.SessionList
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import timber.log.debug
import javax.inject.Inject

internal class DataSessionRepository @Inject constructor(
    private val droidKaigiApi: DroidKaigiApi,
    private val googleFormApi: GoogleFormApi,
    private val sessionDatabase: SessionDatabase,
    private val firestore: Firestore,
    private val favoriteToggleWork: FavoriteToggleWork
) : SessionRepository {
    override fun sessionContents(): Flow<SessionContents> {
        val sessionsFlow = sessions()
            .map { sessions ->
                sessions.sortedBy { it.startTime }
            }
        return sessionsFlow.map { sessions ->
            val speechSessions = sessions.filterIsInstance<SpeechSession>()
            SessionContents(
                sessions = SessionList(sessions),
                speakers = speechSessions.flatMap { it.speakers }.distinct(),
                langs = Lang.values().toList(),
                langSupports = LangSupport.values().toList(),
                rooms = sessions.map { it.room }.sortedBy { it.sort }.distinct(),
                category = speechSessions.map { it.category }.distinct(),
                levels = Level.values().toList()
            )
        }
    }

    private fun sessions(): Flow<List<Session>> {
        val sessionsFlow = sessionDatabase.sessions()
            .filter { it.isNotEmpty() }
            .onEach { Timber.debug { "sessionDatabase.sessions" } }
        val allSpeakersFlow = sessionDatabase.allSpeaker()
            .onEach { Timber.debug { "sessionDatabase.allSpeaker" } }
        val fabSessionIdsFlow = firestore.getFavoriteSessionIds()
            .onEach { Timber.debug { "firestore.getFavoriteSessionIds" } }

        return combine(
            sessionsFlow,
            allSpeakersFlow,
            fabSessionIdsFlow
        ) { sessionEntities, speakerEntities, fabSessionIds ->
            val firstDay = DateTime(sessionEntities.first().session.stime)
            val sessions: List<Session> = sessionEntities
                .map { it.toSession(speakerEntities, fabSessionIds, firstDay) }
                .sortedWith(
                    compareBy(
                        { it.startTime.unixMillisLong },
                        { it.room.id }
                    )
                )
            sessions
        }
    }

    override suspend fun toggleFavoriteWithWorker(sessionId: SessionId) {
        favoriteToggleWork.start(sessionId)
    }

    override suspend fun toggleFavorite(sessionId: SessionId) {
        firestore.toggleFavorite(sessionId)
    }

    override suspend fun sessionFeedback(sessionId: String): SessionFeedback {
        return sessionDatabase.sessionFeedbacks()
            .map { it.toSessionFeedback() }
            .firstOrNull { it.sessionId == sessionId } ?: SessionFeedback(
            sessionId = sessionId,
            totalEvaluation = 0,
            relevancy = 0,
            asExpected = 0,
            difficulty = 0,
            knowledgeable = 0,
            comment = "",
            submitted = false
        )
    }

    override suspend fun saveSessionFeedback(sessionFeedback: SessionFeedback) {
        sessionDatabase.saveSessionFeedback(sessionFeedback)
    }

    override suspend fun submitSessionFeedback(
        session: SpeechSession,
        sessionFeedback: SessionFeedback
    ) {
        val response = googleFormApi.submitSessionFeedback(
            sessionId = session.id.id,
            sessionTitle = session.title.ja,
            totalEvaluation = sessionFeedback.totalEvaluation,
            relevancy = sessionFeedback.relevancy,
            asExpected = sessionFeedback.asExpected,
            difficulty = sessionFeedback.difficulty,
            knowledgeable = sessionFeedback.knowledgeable,
            comment = sessionFeedback.comment
        )
        // TODO: save local db if success feedback POST
    }

    override suspend fun refresh() {
        val response = droidKaigiApi.getSessions()
        sessionDatabase.save(response)
    }

    override fun thumbsUpCounts(sessionId: SessionId): Flow<Int> {
        return firestore.getThumbsUpCount(sessionId)
    }

    override suspend fun incrementThumbsUpCount(
        sessionId: SessionId,
        count: Int
    ) {
        firestore.incrementThumbsUpCount(
            sessionId = sessionId,
            count = count
        )
    }
}
