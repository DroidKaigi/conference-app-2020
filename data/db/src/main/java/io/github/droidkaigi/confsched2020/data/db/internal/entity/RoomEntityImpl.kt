package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import io.github.droidkaigi.confsched2020.data.db.entity.RoomEntity

internal data class RoomEntityImpl(
    @ColumnInfo(name = "room_id")
    override val id: Int,
    @ColumnInfo(name = "room_name")
    override val name: String,
    @ColumnInfo(name = "room_name_en")
    override val enName: String,
    @ColumnInfo(name = "room_sort")
    override val sort: Int
) : RoomEntity
