package io.github.droidkaigi.confsched2020.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class StaffItemResponseImpl(
    override val id: String?,
    override val name: String?,
    override val iconUrl: String?,
    override val profileUrl: String?
) : StaffItemResponse
