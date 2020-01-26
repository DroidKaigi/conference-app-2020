package io.github.droidkaigi.confsched2020.data.firestore.internal

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import io.github.droidkaigi.confsched2020.data.firestore.Firestore
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.Shard
import io.github.droidkaigi.confsched2020.model.ThumbsUpCounter
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
import kotlin.math.floor
import kotlin.math.sin

internal class FirestoreImpl @Inject constructor() : Firestore {
    companion object {
        const val NUM_SHARDS = 10
    }

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

    override fun thumbsUp(sessionId: SessionId): Flow<Int> {
        return flow {
            signInIfNeeded()
            val counterRef = createThumbsUpCounterIfNeeded(sessionId = sessionId, numShards = NUM_SHARDS)
            incrementThumbsUpCount(counterRef)
            emit(getThumbsUpCount(counterRef))
        }
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

    private suspend fun createThumbsUpCounterIfNeeded(sessionId: SessionId, numShards: Int): DocumentReference {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUserId = firebaseAuth.currentUser?.uid ?: throw RuntimeException(
            "RuntimeException"
        )
        val counterDocumentRef = FirebaseFirestore
            .getInstance()
            .collection("confsched/2020/sessions/$sessionId/thumbsup_counters")
            .document(firebaseUserId)

        if (counterDocumentRef.fastGet().exists()) {
            Timber.debug { "createThumbsUpCounterIfNeeded counter already exists" }
            return counterDocumentRef
        }

        counterDocumentRef.set(ThumbsUpCounter(numShards))
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }

                val tasks = arrayListOf<Task<Void>>()

                // Initialize each shard with count=0
                for (i in 0 until numShards) {
                    val makeShard = counterDocumentRef.collection("shards")
                        .document(i.toString())
                        .set(Shard(count = 0))

                    tasks.add(makeShard)
                }

                Tasks.whenAll(tasks)
            }.await()

        Timber.debug { "createThumbsUpCounterIfNeeded creating counter completed" }
        return counterDocumentRef
    }

    private fun incrementThumbsUpCount(counterRef: DocumentReference) {
        val shardId = floor(Math.random() * NUM_SHARDS).toInt()
        counterRef.collection("shards")
            .document(shardId.toString())
            .update("count", FieldValue.increment(1))
    }

    private suspend fun getThumbsUpCount(counterRef: DocumentReference): Int {
        val shards = counterRef.collection("shards").fastGet()
        var count = 0
        shards.forEach { snap ->
            val shard = snap.toObject(Shard::class.java)
            count += shard.count
        }
        return count
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
