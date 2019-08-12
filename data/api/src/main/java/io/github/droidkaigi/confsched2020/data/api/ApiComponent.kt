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

    @Component.Builder
    interface Builder {
        @BindsInstance fun context(context: Context): Builder

        fun build(): ApiComponent
    }

    companion object {
        fun builder(): Builder = DaggerApiComponent.builder()
    }
}
