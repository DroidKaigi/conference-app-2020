package io.github.droidkaigi.confsched2020.di

import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.data.firestore.Firestore
import io.github.droidkaigi.confsched2020.data.firestore.FirestoreComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
object FirestoreComponentModule {
    @Provides @Singleton fun provideRepository(): Firestore {
        return FirestoreComponent.factory()
            .create(Dispatchers.IO)
            .firestore()
    }
}
