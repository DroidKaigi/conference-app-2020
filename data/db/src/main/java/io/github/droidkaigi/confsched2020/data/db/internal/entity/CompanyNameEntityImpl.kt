package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import io.github.droidkaigi.confsched2020.data.db.entity.CompanyNameEntity

internal data class CompanyNameEntityImpl(
    @ColumnInfo(name = "company_name_ja")
    override var jaName: String,
    @ColumnInfo(name = "company_name_en")
    override var enName: String
) : CompanyNameEntity
