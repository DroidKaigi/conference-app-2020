package io.github.droidkaigi.confsched2020.data.api.response

interface CategoryItemResponse {
    val name: String?
    val id: Int?
    val sort: Int?
    val translatedName: TranslatedName?
}
