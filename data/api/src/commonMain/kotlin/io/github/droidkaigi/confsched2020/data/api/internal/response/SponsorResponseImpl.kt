package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.SponsorResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class SponsorResponseImpl(
    override val id: Int,
    override val plan: String,
    override val planDetail: String,
    override val companyUrl: String,
    override val companyName: CompanyNameResponseImpl,
    override val companyLogoUrl: String,
    override val hasBooth: Boolean,
    override val sort: Int
) : SponsorResponse
