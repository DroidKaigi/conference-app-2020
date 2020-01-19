package io.github.droidkaigi.confsched2020.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.GoogleFormApi
import io.github.droidkaigi.confsched2020.data.db.AnnouncementDatabase
import io.github.droidkaigi.confsched2020.data.db.ContributorDatabase
import io.github.droidkaigi.confsched2020.data.db.SessionDatabase
import io.github.droidkaigi.confsched2020.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.data.firestore.Firestore
import io.github.droidkaigi.confsched2020.data.repository.RepositoryComponent
import io.github.droidkaigi.confsched2020.model.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2020.model.repository.ContributorRepository
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.model.repository.SponsorRepository
import io.github.droidkaigi.confsched2020.model.repository.StaffRepository
import javax.inject.Singleton

@Module
object RepositoryComponentModule {
    @Provides @Singleton fun provideRepository(
        repositoryComponent: RepositoryComponent
    ): SessionRepository {
        return repositoryComponent.sessionRepository()
    }

    @Provides @Singleton fun provideSponsorRepository(
        repositoryComponent: RepositoryComponent
    ): SponsorRepository {
        return repositoryComponent.sponsorRepository()
    }

    @Provides @Singleton fun provideAnnouncementRepository(
        repositoryComponent: RepositoryComponent
    ): AnnouncementRepository {
        return repositoryComponent.announcementRepository()
    }

    @Provides @Singleton fun provideStaffRepository(
        repositoryComponent: RepositoryComponent
    ): StaffRepository {
        return repositoryComponent.staffRepository()
    }

    @Provides @Singleton fun provideContributorRepository(
        repositoryComponent: RepositoryComponent
    ): ContributorRepository {
        return repositoryComponent.contributorRepository()
    }

    @Provides @Singleton fun provideRepositoryComponent(
        context: Context,
        droidKaigiApi: DroidKaigiApi,
        googleFormApi: GoogleFormApi,
        sessionDatabase: SessionDatabase,
        sponsorDatabase: SponsorDatabase,
        announcementDatabase: AnnouncementDatabase,
        staffDatabase: StaffDatabase,
        contributorDatabase: ContributorDatabase,
        firestore: Firestore
    ): RepositoryComponent {
        return RepositoryComponent.factory()
            .create(
                context = context,
                droidKaigiApi = droidKaigiApi,
                googleFormApi = googleFormApi,
                sessionDatabase = sessionDatabase,
                sponsorDatabase = sponsorDatabase,
                announcementDatabase = announcementDatabase,
                staffDatabase = staffDatabase,
                contributorDatabase = contributorDatabase,
                firestore = firestore
            )
    }
}
