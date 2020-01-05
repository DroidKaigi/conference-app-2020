package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import io.github.droidkaigi.confsched2020.data.db.entity.CategoryEntity

internal data class CategoryEntityImpl(
    @ColumnInfo(name = "category_id")
    override var id: Int,
    @ColumnInfo(name = "category_name")
    override var name: String,
    @ColumnInfo(name = "category_name_en")
    override var enName: String
) : CategoryEntity
