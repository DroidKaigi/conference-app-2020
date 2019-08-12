package io.github.droidkaigi.confsched2020.data.firestore.internal

import dagger.Binds
import dagger.Module
import io.github.droidkaigi.confsched2020.data.firestore.Firestore

@Module(includes = [FirestoreModule.Providers::class])
internal abstract class FirestoreModule {
    @Binds abstract fun firestore(impl: FirestoreImpl): Firestore

    @Module
    internal object Providers
}
