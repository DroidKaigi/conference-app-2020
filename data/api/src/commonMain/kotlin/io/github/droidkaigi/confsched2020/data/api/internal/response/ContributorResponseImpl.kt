package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.ContributorResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class ContributorResponseImpl(
    override val contributors: List<ContributorItemResponseImpl>
) : ContributorResponse
