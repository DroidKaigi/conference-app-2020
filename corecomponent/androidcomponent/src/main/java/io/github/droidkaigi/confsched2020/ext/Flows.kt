package io.github.droidkaigi.confsched2020.ext

import io.github.droidkaigi.confsched2020.model.LoadingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.toLoadingState(): Flow<LoadingState<T>> {
    return map<T,LoadingState<T>> { LoadingState.Loaded(it) }
        .onStart {
            @Suppress("UNCHECKED_CAST")
            emit(LoadingState.Loading as LoadingState<T>)
        }
        .catch { e ->
            emit(LoadingState.Error<T>(e))
        }
}
