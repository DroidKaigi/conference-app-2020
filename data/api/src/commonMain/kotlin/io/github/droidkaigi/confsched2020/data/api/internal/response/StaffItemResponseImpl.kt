package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.StaffItemResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class StaffItemResponseImpl(
    override val id: String?,
    override val name: String?,
    override val iconUrl: String?,
    override val profileUrl: String?
) : StaffItemResponse
