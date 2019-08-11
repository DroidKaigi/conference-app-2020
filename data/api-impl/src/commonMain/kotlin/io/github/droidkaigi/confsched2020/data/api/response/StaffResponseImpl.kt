package io.github.droidkaigi.confsched2020.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class StaffResponseImpl(
    override val staffs: List<StaffItemResponseImpl>
) : StaffResponse
