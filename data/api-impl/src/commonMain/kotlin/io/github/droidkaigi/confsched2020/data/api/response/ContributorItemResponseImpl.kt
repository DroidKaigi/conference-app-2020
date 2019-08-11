package io.github.droidkaigi.confsched2020.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ContributorItemResponseImpl(
    override val id: Int,
    override val name: String,
    override val iconUrl: String,
    override val profileUrl: String,
    override val type: String
) : ContributorItemResponse
