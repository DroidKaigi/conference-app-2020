package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.LinkResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class LinkResponseImpl(
    override val linkType: String?,
    override val title: String?,
    override val url: String?
) : LinkResponse
