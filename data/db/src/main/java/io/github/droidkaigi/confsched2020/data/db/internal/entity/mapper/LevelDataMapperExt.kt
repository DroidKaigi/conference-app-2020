package io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper

import io.github.droidkaigi.confsched2020.data.db.entity.LevelEntity
import io.github.droidkaigi.confsched2020.data.db.internal.entity.LevelEntityImpl
import io.github.droidkaigi.confsched2020.model.Level

internal fun List<String>.toLevelEntity(): LevelEntityImpl {
    return LevelEntityImpl(
        isBeginner = contains(Level.BEGINNER.name),
        isIntermediate = contains(Level.INTERMEDIATE.name),
        isAdvanced = contains(Level.ADVANCED.name)
    )
}
