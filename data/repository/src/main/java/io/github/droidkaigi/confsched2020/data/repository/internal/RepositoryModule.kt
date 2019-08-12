package io.github.droidkaigi.confsched2020.data.repository.internal

import dagger.Binds
import dagger.Module
import io.github.droidkaigi.confsched2020.data.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2020.data.repository.ContributorRepository
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.data.repository.SponsorRepository
import io.github.droidkaigi.confsched2020.data.repository.StaffRepository

@Module(includes = [RepositoryModule.Providers::class])
internal abstract class RepositoryModule {
    @Binds abstract fun sessionDatabase(impl: DataSessionRepository): SessionRepository

    @Binds abstract fun sponsorDatabase(impl: DataSponsorRepository): SponsorRepository

    @Binds
    abstract fun announcementDatabase(impl: DataAnnouncementRepository): AnnouncementRepository

    @Binds abstract fun contributorDatabase(impl: DataContributorRepository): ContributorRepository

    @Binds abstract fun staffDatabase(impl: DataStaffRepository): StaffRepository

    @Module
    internal object Providers
}
