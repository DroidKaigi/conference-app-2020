package io.github.droidkaigi.confsched2020.data.db.internal.dao

import androidx.annotation.CheckResult
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SessionSpeakerJoinEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SessionWithSpeakersImpl
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

@Dao
internal abstract class SessionSpeakerJoinDao {
    @Language("RoomSql")
    @Transaction
    @CheckResult
    @Query("SELECT * FROM session")
    abstract suspend fun getAllSessions():
        List<SessionWithSpeakersImpl>

    @CheckResult
    @Query("SELECT * FROM session")
    abstract fun getAllSessionsFlow():
        Flow<List<SessionWithSpeakersImpl>>

    @Insert abstract fun insert(sessionSpeakerJoin: List<SessionSpeakerJoinEntityImpl>)

    @Query("DELETE FROM session_speaker_join")
    abstract fun deleteAll()
}
