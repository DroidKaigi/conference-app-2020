package io.github.droidkaigi.confsched2020.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.data.db.AnnouncementDatabase
import io.github.droidkaigi.confsched2020.data.db.ContributorDatabase
import io.github.droidkaigi.confsched2020.data.db.DbComponent
import io.github.droidkaigi.confsched2020.data.db.SessionDatabase
import io.github.droidkaigi.confsched2020.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
object DbComponentModule {
    @Provides @Singleton fun provideItemStore(
        application: Application
    ): SessionDatabase {
        return DbComponent.factory()
            .create(application, Dispatchers.IO, "droidkaigi.db")
            .sessionDatabase()
    }

    @Provides @Singleton fun provideSponsorStore(
        application: Application
    ): SponsorDatabase {
        return DbComponent.factory()
            .create(application, Dispatchers.IO, "droidkaigi.db")
            .sponsorDatabase()
    }

    @Provides @Singleton fun provideAnnouncementStore(
        application: Application
    ): AnnouncementDatabase {
        return DbComponent.factory()
            .create(application, Dispatchers.IO, "droidkaigi.db")
            .announcementDatabase()
    }

    @Provides @Singleton fun provideStaffStore(
        application: Application
    ): StaffDatabase {
        return DbComponent.factory()
            .create(application, Dispatchers.IO, "droidkaigi.db")
            .staffDatabase()
    }

    @Provides @Singleton fun provideContributorStore(
        application: Application
    ): ContributorDatabase {
        return DbComponent.factory()
            .create(application, Dispatchers.IO, "droidkaigi.db")
            .contributorDatabase()
    }
}
