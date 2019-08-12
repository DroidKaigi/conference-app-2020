package io.github.droidkaigi.confsched2020.data.db.internal.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2020.data.db.internal.entity.ContributorEntityImpl

@Dao
internal abstract class ContributorDao {
    @Query("SELECT * FROM contributor order by contributor_order asc")
    abstract suspend fun allContributors(): List<ContributorEntityImpl>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(contributorList: List<ContributorEntityImpl>)

    @Query("DELETE FROM contributor")
    abstract fun deleteAll()
}
