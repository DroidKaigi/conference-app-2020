package io.github.droidkaigi.confsched2020.data.firestore.internal

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import io.github.droidkaigi.confsched2020.data.firestore.Firestore
import io.github.droidkaigi.confsched2020.model.SessionId
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import timber.log.debug

internal class FirestoreImpl @Inject constructor() : Firestore {

    override fun getFavoriteSessionIds(): Flow<List<String>> {
        val setupFavorites = flow {
            signInIfNeeded()
            val favoritesRef = getFavoritesRef()
            val snapshot = favoritesRef
                .fastGet()
            if (snapshot.isEmpty) {
                favoritesRef.add(mapOf("initialized" to true)).await()
            }
            emit(favoritesRef)
        }
        val favoritesSnapshotFlow = setupFavorites.flatMapLatest {
            it.whereEqualTo("favorite", true).toFlow()
        }
        return favoritesSnapshotFlow.mapLatest { favorites ->
            Timber.debug { "favoritesSnapshotFlow onNext" }
            favorites.mapNotNull { item -> item.id }
        }
    }

    override suspend fun toggleFavorite(sessionId: SessionId) {
        Timber.debug { "toggleFavorite: start" }
        signInIfNeeded()
        val document = getFavoritesRef().document(sessionId.id).fastGet()
        val nowFavorite = document.exists() && (document.data?.get(sessionId.id) == true)
        val newFavorite = !nowFavorite
        if (document.exists()) {
            Timber.debug { "toggleFavorite: $sessionId document exits" }
            document.reference
                .delete()
                .await()
        } else {
            Timber.debug { "toggleFavorite: $sessionId document not exits" }
            document.reference
                .set(mapOf("favorite" to newFavorite))
                .await()
        }
        Timber.debug { "toggleFavorite: end" }
    }

    private fun getFavoritesRef(): CollectionReference {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUserId = firebaseAuth.currentUser?.uid ?: throw RuntimeException(
            "RuntimeException"
        )
        return FirebaseFirestore
            .getInstance()
            .collection("confsched/2020/users/$firebaseUserId/favorites")
    }

    private suspend fun signInIfNeeded() {
        Timber.debug { "signInIfNeeded start" }
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            Timber.debug { "signInIfNeeded user already exists" }
            return
        }
        firebaseAuth.signInAnonymously().await()
        Timber.debug { "signInIfNeeded end" }
    }
}

private suspend fun DocumentReference.fastGet(): DocumentSnapshot {
    return try {
        get(Source.CACHE).await()
    } catch (e: Exception) {
        get(Source.SERVER).await()
    }
}

private suspend fun Query.fastGet(): QuerySnapshot {
    return try {
        get(Source.CACHE).await()
    } catch (e: Exception) {
        get(Source.SERVER).await()
    }
}

private suspend fun CollectionReference.fastGet(): QuerySnapshot {
    return try {
        get(Source.CACHE).await()
    } catch (e: Exception) {
        get(Source.SERVER).await()
    }
}

private fun Query.toFlow(): Flow<QuerySnapshot> {
    return callbackFlow<QuerySnapshot> {
        val listenerRegistration = addSnapshotListener { snapshot, exception ->
            if (exception != null) close(exception)
            else if (snapshot != null) {
                offer(snapshot)
            }
        }
        awaitClose { listenerRegistration.remove() }
    }
}
