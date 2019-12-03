package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.droidkaigi.confsched2020.data.db.entity.SponsorEntity

@Entity(tableName = "sponsor")
internal data class SponsorEntityImpl(
    @PrimaryKey
    override val id: Int,
    override val plan: String,
    override val planDetail: String,
    override val companyUrl: String,
    @Embedded override val companyName: CompanyNameEntityImpl,
    override val companyLogoUrl: String,
    override val hasBooth: Boolean,
    override val sort: Int
) : SponsorEntity
