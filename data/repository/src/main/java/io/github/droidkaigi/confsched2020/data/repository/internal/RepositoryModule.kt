package io.github.droidkaigi.confsched2020.data.repository.internal

import dagger.Binds
import dagger.Module
import io.github.droidkaigi.confsched2020.model.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2020.model.repository.ContributorRepository
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.model.repository.SponsorRepository
import io.github.droidkaigi.confsched2020.model.repository.StaffRepository

@Module(includes = [RepositoryModule.Providers::class])
internal abstract class RepositoryModule {
    @Binds abstract fun sessionRepository(impl: DataSessionRepository): SessionRepository

    @Binds abstract fun sponsorRepository(impl: DataSponsorRepository): SponsorRepository

    @Binds
    abstract fun announcementRepository(impl: DataAnnouncementRepository): AnnouncementRepository

    @Binds abstract fun contributorRepository(
        impl: DataContributorRepository
    ): ContributorRepository

    @Binds abstract fun staffRepository(impl: DataStaffRepository): StaffRepository

    @Module
    internal object Providers
}
