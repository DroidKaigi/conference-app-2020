package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.TranslatedName
import kotlinx.serialization.Serializable

@Serializable
internal data class TranslatedNameImpl(
    override val ja: String,
    override val en: String
) : TranslatedName
