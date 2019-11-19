package io.github.droidkaigi.confsched2020.data.db.internal.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2020.data.db.internal.entity.AnnouncementEntityImpl
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class AnnouncementDao {
    @Query("SELECT * FROM announcement WHERE lower(lang) = lower(:lang)")
    abstract fun announcementsByLang(lang: String): Flow<List<AnnouncementEntityImpl>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(announcements: List<AnnouncementEntityImpl>)

    @Query("DELETE FROM announcement")
    abstract fun deleteAll()
}
