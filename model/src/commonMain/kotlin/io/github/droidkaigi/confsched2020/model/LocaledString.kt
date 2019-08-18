package io.github.droidkaigi.confsched2020.model

@AndroidParcelize
data class LocaledString(
    val ja: String,
    val en: String
) : AndroidParcel {
    fun getByLang(lang: Lang): String {
        return if (lang == Lang.JA) {
            ja
        } else {
            en
        }
    }
}
