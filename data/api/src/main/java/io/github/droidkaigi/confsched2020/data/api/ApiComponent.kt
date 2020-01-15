package io.github.droidkaigi.confsched2020.data.api

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.github.droidkaigi.confsched2020.data.api.internal.ApiModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class
    ]
)
interface ApiComponent {
    fun DroidKaigiApi(): DroidKaigiApi
    fun GoogleFormApi(): GoogleFormApi

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApiComponent
    }

    companion object {
        fun factory(): Factory = DaggerApiComponent.factory()
    }
}
