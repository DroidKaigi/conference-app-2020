package io.github.droidkaigi.confsched2020.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.data.api.ApiComponent
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.GoogleFormApi
import javax.inject.Singleton

@Module
object ApiComponentModule {
    @Provides
    @Singleton
    fun provideDroidKaigiApi(application: Application): DroidKaigiApi {
        return ApiComponent.factory()
            .create(application)
            .DroidKaigiApi()
    }

    @Provides
    @Singleton
    fun provideGoogleFormApi(application: Application): GoogleFormApi {
        return ApiComponent.factory()
            .create(application)
            .GoogleFormApi()
    }
}
