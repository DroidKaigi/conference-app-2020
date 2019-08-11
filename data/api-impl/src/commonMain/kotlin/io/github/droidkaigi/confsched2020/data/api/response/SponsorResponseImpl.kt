package io.github.droidkaigi.confsched2020.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class SponsorResponseImpl(
    override val platinum: List<SponsorItemResponseImpl>,
    override val gold: List<SponsorItemResponseImpl>,
    override val support: List<SponsorItemResponseImpl>,
    override val tech: List<SponsorItemResponseImpl>
) : SponsorResponse
