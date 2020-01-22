package io.github.droidkaigi.confsched2020.data.db.internal

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.droidkaigi.confsched2020.data.db.internal.dao.AnnouncementDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.ContributorDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SessionDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SessionSpeakerJoinDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SpeakerDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SponsorDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.StaffDao
import io.github.droidkaigi.confsched2020.data.db.internal.entity.AnnouncementEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.ContributorEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SessionEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SessionSpeakerJoinEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SpeakerEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SponsorEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.StaffEntityImpl

@Database(
    entities = [
        (SessionEntityImpl::class),
        (SpeakerEntityImpl::class),
        (SessionSpeakerJoinEntityImpl::class),
        (SponsorEntityImpl::class),
        (AnnouncementEntityImpl::class),
        (StaffEntityImpl::class),
        (ContributorEntityImpl::class)
    ],
    version = 17
)
internal abstract class CacheDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun speakerDao(): SpeakerDao
    abstract fun sessionSpeakerJoinDao(): SessionSpeakerJoinDao
    abstract fun sponsorDao(): SponsorDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun staffDao(): StaffDao
    abstract fun contributorDao(): ContributorDao
    fun sqlite(): SupportSQLiteDatabase {
        return mDatabase
    }
}
