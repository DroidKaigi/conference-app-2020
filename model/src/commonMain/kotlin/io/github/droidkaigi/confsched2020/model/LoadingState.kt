package io.github.droidkaigi.confsched2020.model

sealed class LoadingState {
    object Initialized : LoadingState()
    object Loading : LoadingState()
    object Loaded : LoadingState()
    class Error(val e: Throwable) : LoadingState()

    val isLoading get() = this is Loading
    fun getExceptionIfExists() = if (this is Error) e else null
}