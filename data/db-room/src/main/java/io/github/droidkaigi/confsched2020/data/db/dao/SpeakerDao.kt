package io.github.droidkaigi.confsched2020.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2020.data.db.entity.SpeakerEntityImpl
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SpeakerDao {
    @Query("SELECT * FROM speaker")
    abstract suspend fun getAllSpeaker(): List<SpeakerEntityImpl>

    @Query("SELECT * FROM speaker")
    abstract fun getAllSpeakerFlow(): Flow<List<SpeakerEntityImpl>>

    @Query("DELETE FROM speaker")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(speakers: List<SpeakerEntityImpl>)
}
