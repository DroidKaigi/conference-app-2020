package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.AnnouncementResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class AnnouncementResponseImpl(
    override val id: Long,
    override val title: String,
    override val content: String,
    override val type: String,
    override val publishedAt: String,
    override val language: String
) : AnnouncementResponse
