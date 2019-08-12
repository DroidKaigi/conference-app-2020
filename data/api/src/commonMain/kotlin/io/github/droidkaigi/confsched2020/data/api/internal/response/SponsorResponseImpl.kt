package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.SponsorResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class SponsorResponseImpl(
    override val platinum: List<SponsorItemResponseImpl>,
    override val gold: List<SponsorItemResponseImpl>,
    override val support: List<SponsorItemResponseImpl>,
    override val tech: List<SponsorItemResponseImpl>
) : SponsorResponse
