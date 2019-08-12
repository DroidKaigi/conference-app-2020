package io.github.droidkaigi.confsched2020.model

sealed class SessionLoadingState {
    object Initialized : SessionLoadingState()
    object Loading : SessionLoadingState()
    class Loaded(val sessionContents: SessionContents) : SessionLoadingState()

    val isInitialized: Boolean get() = this == SessionLoadingState.Initialized
    val isLoading get() = this == SessionLoadingState.Loading
    val isLoaded: Boolean get() = this is SessionLoadingState.Loaded
}
