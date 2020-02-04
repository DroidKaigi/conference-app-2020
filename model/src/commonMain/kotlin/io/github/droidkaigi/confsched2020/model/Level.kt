package io.github.droidkaigi.confsched2020.model

enum class Level(val rawValue: LocaledString) {
    BEGINNER(LocaledString("初級", "Beginner")),
    INTERMEDIATE(LocaledString("中級", "Intermediate")),
    ADVANCED(LocaledString("上級", "Advanced"));

    companion object {
        fun findLevel(name: String): Level {
            // if it returns null, the sever response is broken.
            return values().find { it.name == name }!!
        }
    }
}
