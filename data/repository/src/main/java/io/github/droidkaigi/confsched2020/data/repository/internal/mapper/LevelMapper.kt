package io.github.droidkaigi.confsched2020.data.repository.internal.mapper

import io.github.droidkaigi.confsched2020.data.db.entity.LevelEntity
import io.github.droidkaigi.confsched2020.model.Level

fun LevelEntity.toLevels(): List<Level> {
    val entityValues = listOf(isBeginner, isIntermediate, isAdvanced)
    val modelValues = Level.values()
    return entityValues.zip(modelValues).filter { it.first }.map { it.second }
}
