package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.droidkaigi.confsched2020.data.db.entity.ContributorEntity

@Entity(tableName = "contributor")
internal class ContributorEntityImpl(
    @PrimaryKey
    override val id: Int,
    @ColumnInfo(name = "contributor_name")
    override val name: String,
    @ColumnInfo(name = "contributor_icon_url")
    override val iconUrl: String,
    @ColumnInfo(name = "contributor_profile_url")
    override val profileUrl: String,
    @ColumnInfo(name = "contributor_order")
    override val order: Int
) : ContributorEntity
