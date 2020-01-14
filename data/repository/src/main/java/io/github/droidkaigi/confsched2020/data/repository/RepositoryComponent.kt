package io.github.droidkaigi.confsched2020.data.repository

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.GoogleFormApi
import io.github.droidkaigi.confsched2020.data.db.AnnouncementDatabase
import io.github.droidkaigi.confsched2020.data.db.ContributorDatabase
import io.github.droidkaigi.confsched2020.data.db.SessionDatabase
import io.github.droidkaigi.confsched2020.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.data.firestore.Firestore
import io.github.droidkaigi.confsched2020.data.repository.internal.RepositoryModule
import io.github.droidkaigi.confsched2020.model.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2020.model.repository.ContributorRepository
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.model.repository.SponsorRepository
import io.github.droidkaigi.confsched2020.model.repository.StaffRepository
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RepositoryModule::class
    ]
)
interface RepositoryComponent {
    fun sessionRepository(): SessionRepository
    fun sponsorRepository(): SponsorRepository
    fun announcementRepository(): AnnouncementRepository
    fun staffRepository(): StaffRepository
    fun contributorRepository(): ContributorRepository

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance droidKaigiApi: DroidKaigiApi,
            @BindsInstance googleFormApi: GoogleFormApi,
            @BindsInstance sessionDatabase: SessionDatabase,
            @BindsInstance sponsorDatabase: SponsorDatabase,
            @BindsInstance announcementDatabase: AnnouncementDatabase,
            @BindsInstance staffDatabase: StaffDatabase,
            @BindsInstance contributorDatabase: ContributorDatabase,
            @BindsInstance firestore: Firestore
        ): RepositoryComponent
    }

    companion object {
        fun factory(): Factory = DaggerRepositoryComponent.factory()
    }
}
