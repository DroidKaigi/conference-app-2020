package io.github.droidkaigi.confsched2020.data.firestore
import kotlinx.coroutines.flow.Flow

interface Firestore {
    suspend fun getFavoriteSessionIds(): Flow<List<String>>
    suspend fun toggleFavorite(sessionId: String)
}
