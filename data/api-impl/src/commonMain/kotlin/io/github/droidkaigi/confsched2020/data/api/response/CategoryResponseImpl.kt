package io.github.droidkaigi.confsched2020.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponseImpl(
    override val id: Int?,
    override val sort: Int?,
    override val title: String?,
    override val items: List<CategoryItemResponseImpl?>?
) : CategoryResponse
