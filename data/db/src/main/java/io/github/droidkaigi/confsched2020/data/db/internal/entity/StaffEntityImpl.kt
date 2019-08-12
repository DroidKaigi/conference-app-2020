package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.droidkaigi.confsched2020.data.db.entity.StaffEntity

@Entity(tableName = "staff")
internal class StaffEntityImpl(
    @PrimaryKey override var id: String,
    @ColumnInfo(name = "staff_name")
    override var name: String,
    @ColumnInfo(name = "staff_icon_url")
    override var iconUrl: String?,
    @ColumnInfo(name = "staff_profile_url")
    override var profileUrl: String?
) : StaffEntity
