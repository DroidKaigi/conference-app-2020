package io.github.droidkaigi.confsched2020.model

enum class Level(val id: String, val rawValue: LocaledString) {
    BEGINNER("BEGINNER", LocaledString("初級", "Beginner")),
    INTERMEDIATE("INTERMEDIATE", LocaledString("中級", "Intermediate")),
    ADVANCED("ADVANCED", LocaledString("上級", "Advanced"));

    companion object {
        fun findLevel(name: String): Level {
            // if it returns null, the sever response is broken.
            return values().find { it.id == name }!!
        }
    }
}
