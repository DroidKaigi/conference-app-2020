package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.SessionResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class SessionResponseImpl(
    override val id: String,
    override val isServiceSession: Boolean,
    override val title: LocaledResponseImpl?,
    override val speakers: List<String>,
    override val description: String?,
    override val startsAt: String?,
    override val endsAt: String?,
    override val language: String?,
    override val roomId: Int?,
    override val sessionCategoryItemId: Int?,
    override val sessionType: String?,
    override val message: SessionMessageResponseImpl? = null,
    override val isPlenumSession: Boolean,
    override val targetAudience: String,
    override val interpretationTarget: Boolean,
    override val videoUrl: String? = null,
    override val slideUrl: String? = null
) : SessionResponse
