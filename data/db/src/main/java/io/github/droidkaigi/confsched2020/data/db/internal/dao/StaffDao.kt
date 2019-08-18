package io.github.droidkaigi.confsched2020.data.db.internal.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2020.data.db.internal.entity.StaffEntityImpl
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class StaffDao {
    @Query("SELECT * FROM staff")
    abstract fun allStaffs(): Flow<List<StaffEntityImpl>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(staffs: List<StaffEntityImpl>)

    @Query("DELETE FROM staff")
    abstract fun deleteAll()
}
