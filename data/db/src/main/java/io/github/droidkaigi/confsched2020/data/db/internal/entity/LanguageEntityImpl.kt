package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import io.github.droidkaigi.confsched2020.data.db.entity.LanguageEntity

internal data class LanguageEntityImpl(
    @ColumnInfo(name = "language_id")
    override var id: Int,
    @ColumnInfo(name = "language_name")
    override var name: String,
    @ColumnInfo(name = "language_name_ja")
    override var jaName: String,
    @ColumnInfo(name = "language_name_en")
    override var enName: String
) : LanguageEntity
