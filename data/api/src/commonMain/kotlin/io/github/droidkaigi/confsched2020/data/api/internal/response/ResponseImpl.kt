package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.Response
import kotlinx.serialization.Serializable

@Serializable
internal data class ResponseImpl(
    override val sessions: List<SessionResponseImpl>,
    override val rooms: List<RoomResponseImpl>?,
    override val speakers: List<SpeakerResponseImpl>?,
    override val categories: List<CategoryResponseImpl>?
) : Response
