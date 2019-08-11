package io.github.droidkaigi.confsched2020.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class LinkResponseImpl(
    override val linkType: String?,
    override val title: String?,
    override val url: String?
) : LinkResponse
