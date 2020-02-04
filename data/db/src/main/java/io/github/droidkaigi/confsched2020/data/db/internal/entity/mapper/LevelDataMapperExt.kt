package io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper

import io.github.droidkaigi.confsched2020.data.db.internal.entity.LevelEntityImpl
import io.github.droidkaigi.confsched2020.model.Level

internal fun List<String>.toLevelEntity(): LevelEntityImpl {
    return LevelEntityImpl(
        isBeginner = contains(Level.BEGINNER.id),
        isIntermediate = contains(Level.INTERMEDIATE.id),
        isAdvanced = contains(Level.ADVANCED.id)
    )
}
