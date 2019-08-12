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

    @Component.Builder
    interface Builder {

        @BindsInstance fun coroutineContext(coroutineContext: CoroutineContext): Builder

        fun build(): FirestoreComponent
    }

    companion object {
        fun builder(): Builder = DaggerFirestoreComponent.builder()
    }
}
