package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.CategoryResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class CategoryResponseImpl(
    override val id: Int?,
    override val sort: Int?,
    override val title: LocaledResponseImpl?,
    override val items: List<CategoryItemResponseImpl?>?
) : CategoryResponse
