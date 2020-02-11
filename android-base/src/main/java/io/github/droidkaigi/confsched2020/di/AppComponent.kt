package io.github.droidkaigi.confsched2020.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import io.github.droidkaigi.confsched2020.App
import io.github.droidkaigi.confsched2020.MainActivityModule
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.model.repository.ContributorRepository
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        AndroidInjectionModule::class,
        MainActivityModule.MainActivityBuilder::class,
        DbComponentModule::class,
        RepositoryComponentModule::class,
        FirestoreComponentModule::class,
        ApiComponentModule::class,
        DeviceComponentModule::class
    ]
)
interface AppComponent : AndroidInjector<App>, AppComponentInterface {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    override fun inject(app: App)

    override fun sessionRepository(): SessionRepository
    fun contributorRepository(): ContributorRepository
    fun droidKaigiApi(): DroidKaigiApi
    fun staffDatabase(): StaffDatabase
}

fun Application.createAppComponent() = DaggerAppComponent.factory().create(this)
