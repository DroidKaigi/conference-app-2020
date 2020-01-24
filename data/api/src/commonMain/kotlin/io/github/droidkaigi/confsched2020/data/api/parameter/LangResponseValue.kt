package io.github.droidkaigi.confsched2020.data.api.parameter

enum class LangResponseValue(val value: String) {
    JP("JAPANESE"),
    EN("ENGLISH");

    companion object {
        fun of(value: String): LangResponseValue {
            return values().firstOrNull { it.value == value } ?: EN
        }
    }
}
