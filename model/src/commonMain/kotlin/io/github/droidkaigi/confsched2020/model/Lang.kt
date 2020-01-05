package io.github.droidkaigi.confsched2020.model

enum class Lang(val text: LocaledString) {
    EN(LocaledString("英語", "English")),
    JA(LocaledString("日本語", "Japanese"));

    fun getString(en: String, ja: String): String {
        return if (this != JA) {
            en
        } else {
            ja
        }
    }

    companion object {
        fun findLang(name: String): Lang {
            return if (name == "JAPANESE") {
                JA
            } else {
                EN
            }
        }
    }
}

expect fun defaultLang(): Lang
