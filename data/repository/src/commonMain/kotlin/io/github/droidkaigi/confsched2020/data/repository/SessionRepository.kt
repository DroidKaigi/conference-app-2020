package io.github.droidkaigi.confsched2020.data.repository

import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionFeedback
import io.github.droidkaigi.confsched2020.model.SpeechSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun sessionContents(): Flow<SessionContents>
    suspend fun refresh()
    suspend fun toggleFavorite(session: Session)
    suspend fun sessionFeedback(sessionId: String): SessionFeedback
    suspend fun saveSessionFeedback(sessionFeedback: SessionFeedback)
    suspend fun submitSessionFeedback(
        session: SpeechSession,
        sessionFeedback: SessionFeedback
    )
}
