package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.SpeakerResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class SpeakerResponseImpl(
    override val firstName: String?,
    override val lastName: String?,
    override val profilePicture: String?,
    override val sessions: List<Int?>?,
    override val tagLine: String?,
    override val isTopSpeaker: Boolean?,
    override val bio: String?,
    override val fullName: String?,
    override val id: String?
) : SpeakerResponse
