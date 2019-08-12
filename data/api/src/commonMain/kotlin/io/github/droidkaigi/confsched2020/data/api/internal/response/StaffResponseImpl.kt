package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class StaffResponseImpl(
    override val staffs: List<StaffItemResponseImpl>
) : StaffResponse
