package io.github.droidkaigi.confsched2020.data.api.response

interface CategoryResponse {
    val id: Int?
    val sort: Int?
    val title: LocaledResponse?
    val items: List<CategoryItemResponse?>?
}
