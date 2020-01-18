package io.github.droidkaigi.confsched2020.data.db.internal

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.data.db.AnnouncementDatabase
import io.github.droidkaigi.confsched2020.data.db.ContributorDatabase
import io.github.droidkaigi.confsched2020.data.db.SessionDatabase
import io.github.droidkaigi.confsched2020.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.data.db.internal.dao.AnnouncementDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.ContributorDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SessionDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SessionFeedbackDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SessionSpeakerJoinDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SpeakerDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SponsorDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.StaffDao
import javax.inject.Singleton

@Module(includes = [DbModule.Providers::class])
internal abstract class DbModule {
    @Binds abstract fun sessionDatabase(impl: RoomDatabase): SessionDatabase
    @Binds abstract fun sponsorDatabase(impl: RoomDatabase): SponsorDatabase
    @Binds abstract fun announcementDatabase(impl: RoomDatabase): AnnouncementDatabase
    @Binds abstract fun staffDatabase(impl: RoomDatabase): StaffDatabase
    @Binds abstract fun contributorDatabase(impl: RoomDatabase): ContributorDatabase

    @Module
    internal object Providers {
        @Singleton @Provides fun cacheDatabase(
            context: Context,
            filename: String?
        ): CacheDatabase {
            return Room.databaseBuilder(
                context,
                CacheDatabase::class.java,
                filename ?: "droidkaigi.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Singleton @Provides fun sessionFeedbackDatabase(
            context: Context,
            filename: String?
        ): SessionFeedbackDatabase {
            return Room.databaseBuilder(
                context,
                SessionFeedbackDatabase::class.java,
                filename ?: "session_feedback.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides fun sessionDao(database: CacheDatabase): SessionDao {
            return database.sessionDao()
        }

        @Provides fun speakerDao(database: CacheDatabase): SpeakerDao {
            return database.speakerDao()
        }

        @Provides fun sessionSpeakerJoinDao(
            database: CacheDatabase
        ): SessionSpeakerJoinDao {
            return database.sessionSpeakerJoinDao()
        }

        @Provides fun sponsorDao(databaseSponsor: CacheDatabase): SponsorDao {
            return databaseSponsor.sponsorDao()
        }

        @Provides fun announcementDao(database: CacheDatabase): AnnouncementDao {
            return database.announcementDao()
        }

        @Provides fun staffDao(database: CacheDatabase): StaffDao {
            return database.staffDao()
        }

        @Provides fun contributorDao(database: CacheDatabase): ContributorDao {
            return database.contributorDao()
        }

        @Provides fun sessionFeedbackDao(database: SessionFeedbackDatabase): SessionFeedbackDao {
            return database.sessionFeedbackDao()
        }
    }
}
