package io.github.droidkaigi.confsched2020.model

sealed class LoadState<out T> {
    object Loading : LoadState<Nothing>()
    class Loaded<T>(val value: T) : LoadState<T>()
    class Error<T>(val e: Throwable) : LoadState<T>()

    val isLoading get() = this is Loading
    fun getErrorIfExists() = if (this is Error) e else null
}

sealed class LoadingState {
    object Loading : LoadingState()
    object Loaded : LoadingState()
    class Error(val e: Throwable) : LoadingState()

    val isLoading get() = this is Loading
    fun getErrorIfExists() = if (this is Error) e else null
}
