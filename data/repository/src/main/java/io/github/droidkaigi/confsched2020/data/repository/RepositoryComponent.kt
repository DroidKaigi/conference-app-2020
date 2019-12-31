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
    fun favoriteToggleWorkerManager(): FavoriteToggleWorkerManager

    @Component.Builder
    interface Builder {
        @BindsInstance fun context(context: Context): Builder
        @BindsInstance fun droidKaigiApi(api: DroidKaigiApi): Builder
        @BindsInstance fun googleFormApi(api: GoogleFormApi): Builder
        @BindsInstance fun database(database: SessionDatabase): Builder
        @BindsInstance fun sponsorDatabase(database: SponsorDatabase): Builder
        @BindsInstance fun announcementDatabase(database: AnnouncementDatabase): Builder
        @BindsInstance fun staffDatabase(database: StaffDatabase): Builder
        @BindsInstance fun contributorDatabase(database: ContributorDatabase): Builder
        @BindsInstance fun firestore(firestore: Firestore): Builder

        fun build(): RepositoryComponent
    }

    companion object {
        fun builder(): Builder = DaggerRepositoryComponent.builder()
    }
}
