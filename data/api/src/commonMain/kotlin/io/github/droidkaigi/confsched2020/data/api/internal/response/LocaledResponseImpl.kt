package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.LocaledResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class LocaledResponseImpl(
    override val ja: String?,
    override val en: String?
) : LocaledResponse
