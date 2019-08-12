package io.github.droidkaigi.confsched2020.data.db.internal.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SessionEntityImpl

@Dao
internal abstract class SessionDao {
    @Query("SELECT * FROM session")
    abstract fun sessionsLiveData(): LiveData<List<SessionEntityImpl>>

    @Query("SELECT * FROM session")
    abstract fun sessions(): List<SessionEntityImpl>

    @Query("DELETE FROM session")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(sessions: List<SessionEntityImpl>)
}
