package io.github.droidkaigi.confsched2020.data.firestore

import dagger.BindsInstance
import dagger.Component
import io.github.droidkaigi.confsched2020.data.firestore.internal.FirestoreModule
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
@Component(
    modules = [
        FirestoreModule::class
    ]
)
interface FirestoreComponent {
    fun firestore(): Firestore

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance coroutineContext: CoroutineContext): FirestoreComponent
    }

    companion object {
        fun factory(): Factory = DaggerFirestoreComponent.factory()
    }
}
