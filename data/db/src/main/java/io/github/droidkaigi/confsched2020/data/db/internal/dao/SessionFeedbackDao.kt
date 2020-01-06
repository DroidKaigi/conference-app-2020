package io.github.droidkaigi.confsched2020.data.db.internal.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SessionFeedbackEntityImpl

@Dao
internal abstract class SessionFeedbackDao {
    @Query("SELECT * FROM session_feedback")
    abstract suspend fun sessionFeedbacks(): List<SessionFeedbackEntityImpl>

    @Query("DELETE FROM session_feedback WHERE session_id = :sessionId")
    abstract fun delete(sessionId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(sessionFeedback: SessionFeedbackEntityImpl)
}
