package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.ext.asLiveData
import io.github.droidkaigi.confsched2020.ext.composeBy
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionContents
import javax.inject.Inject

class SessionsViewModel @Inject constructor(
    val sessionRepository: SessionRepository
) : ViewModel() {
    // UiModel definition
    data class UiModel(
        val sessionContents: SessionContents?,
        val isLoading: Boolean,
        val error: Error
    ) {
        sealed class Error {
            class FailLoadSessions(val e: Throwable) : Error()
            class FailFavorite(val e: Throwable) : Error()
            object None : Error()
            companion object {
                fun of(
                    sessionContentsLoadState: LoadState<SessionContents>,
                    favoriteLoadingState: LoadingState
                ): Error {
                    if (sessionContentsLoadState is LoadState.Error) {
                        return FailLoadSessions(sessionContentsLoadState.e)
                    }
                    if (favoriteLoadingState is LoadingState.Error) {
                        return FailFavorite(favoriteLoadingState.e)
                    }
                    return None
                }
            }
        }

        companion object {
            val EMPTY = UiModel(null, false, Error.None)
        }
    }

    // LiveDatas
    private val sessionLoadState: LiveData<LoadState<SessionContents>> = liveData {
        emitSource(
            sessionRepository.sessionContents()
                .toLoadingState()
                .asLiveData()
        )
        sessionRepository.refresh()
    }
    private val favoriteLoadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.Initialized)

    // Compose UiModel
    val uiModel: LiveData<UiModel> = composeBy(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionLoadState,
        liveData2 = favoriteLoadingState
    ) { current: UiModel,
        sessionsLoadState: LoadState<SessionContents>,
        favoriteLoadingState: LoadingState ->
        val isLoading = sessionsLoadState.isLoading || favoriteLoadingState.isLoading
        val sessionContents = when (sessionsLoadState) {
            is LoadState.Loaded -> {
                sessionsLoadState.value
            }
            else -> {
                current.sessionContents
            }
        }
        UiModel(
            sessionContents = sessionContents,
            isLoading = isLoading,
            error = UiModel.Error.of(
                sessionContentsLoadState = sessionsLoadState,
                favoriteLoadingState = favoriteLoadingState
            )
        )
    }

    // Functions
    fun favorite(session: Session): LiveData<Unit> {
        return liveData {
            try {
                favoriteLoadingState.value = LoadingState.Loading
                sessionRepository.toggleFavorite(session)
                favoriteLoadingState.value = LoadingState.Loaded
            } catch (e: Exception) {
                favoriteLoadingState.value = LoadingState.Error(e)
            }
        }
    }
}