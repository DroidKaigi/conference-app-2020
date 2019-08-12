package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.ContributorItemResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class ContributorItemResponseImpl(
    override val id: Int,
    override val name: String,
    override val iconUrl: String,
    override val profileUrl: String,
    override val type: String
) : ContributorItemResponse
