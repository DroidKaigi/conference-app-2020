package io.github.droidkaigi.confsched2020.model

sealed class LoadingState<out T> {
    object Loading : LoadingState<Nothing>()
    class Loaded<T>(val value: T) : LoadingState<T>()
    class Error<T>(val e: Throwable) : LoadingState<T>()

    val isLoading get() = this is Loading
}