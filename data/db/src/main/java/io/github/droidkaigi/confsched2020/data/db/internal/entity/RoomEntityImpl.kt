package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import io.github.droidkaigi.confsched2020.data.db.entity.RoomEntity

internal data class RoomEntityImpl(
    @ColumnInfo(name = "room_id")
    override var id: Int,
    @ColumnInfo(name = "room_name")
    override var name: String
) : RoomEntity
