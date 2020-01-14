package io.github.droidkaigi.confsched2020.data.db

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.github.droidkaigi.confsched2020.data.db.internal.DbModule
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
@Component(
    modules = [
        DbModule::class
    ]
)
interface DbComponent {
    fun sessionDatabase(): SessionDatabase
    fun sponsorDatabase(): SponsorDatabase
    fun announcementDatabase(): AnnouncementDatabase
    fun staffDatabase(): StaffDatabase
    fun contributorDatabase(): ContributorDatabase

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance coroutineContext: CoroutineContext,
            @BindsInstance filename: String?
        ): DbComponent
    }

    companion object {
        fun factory(): Factory = DaggerDbComponent.factory()
    }
}
