package io.github.droidkaigi.confsched2020.data.db.internal

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SessionFeedbackDao
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SessionFeedbackEntityImpl

@Database(
    entities = [
        (SessionFeedbackEntityImpl::class)
    ],
    version = 1
)
internal abstract class SessionFeedbackDatabase : RoomDatabase() {
    abstract fun sessionFeedbackDao(): SessionFeedbackDao
}
