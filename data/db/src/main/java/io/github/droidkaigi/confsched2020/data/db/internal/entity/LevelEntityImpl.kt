package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import io.github.droidkaigi.confsched2020.data.db.entity.LevelEntity

internal data class LevelEntityImpl(
    @ColumnInfo(name = "is_beginner")
    override val isBeginner: Boolean,
    @ColumnInfo(name = "is_intermediate")
    override val isIntermediate: Boolean,
    @ColumnInfo(name = "is_advanced")
    override val isAdvanced: Boolean
) : LevelEntity
