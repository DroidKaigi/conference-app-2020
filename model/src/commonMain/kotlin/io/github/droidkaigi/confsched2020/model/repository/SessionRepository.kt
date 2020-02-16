package io.github.droidkaigi.confsched2020.model.repository

import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionFeedback
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.SpeechSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun sessionContents(): Flow<SessionContents>
    suspend fun refresh()
    suspend fun toggleFavoriteWithWorker(sessionId: SessionId)
    suspend fun toggleFavorite(sessionId: SessionId)
    suspend fun sessionFeedback(sessionId: String): SessionFeedback
    suspend fun saveSessionFeedback(sessionFeedback: SessionFeedback)
    suspend fun submitSessionFeedback(
        session: SpeechSession,
        sessionFeedback: SessionFeedback
    )
    fun thumbsUpCounts(sessionId: SessionId): Flow<Int>
    suspend fun incrementThumbsUpCount(sessionId: SessionId, count: Int)
}
