package io.github.droidkaigi.confsched2020.data.db

import io.github.droidkaigi.confsched2020.data.api.response.Response
import io.github.droidkaigi.confsched2020.data.db.entity.SessionFeedbackEntity
import io.github.droidkaigi.confsched2020.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2020.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2020.model.SessionFeedback
import kotlinx.coroutines.flow.Flow

interface SessionDatabase {
    fun sessions(): Flow<List<SessionWithSpeakers>>
    fun allSpeaker(): Flow<List<SpeakerEntity>>
    suspend fun save(apiResponse: Response)
    suspend fun sessionFeedbacks(): List<SessionFeedbackEntity>
    suspend fun saveSessionFeedback(sessionFeedback: SessionFeedback)
}
