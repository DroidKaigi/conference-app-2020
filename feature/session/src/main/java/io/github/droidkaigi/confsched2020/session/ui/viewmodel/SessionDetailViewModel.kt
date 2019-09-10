package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.ext.composeBy
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionId
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class SessionDetailViewModel @AssistedInject constructor(
    @Assisted private val sessionId: SessionId,
    val sessionRepository: SessionRepository
) : ViewModel() {
    // UiModel definition
    data class UiModel(
        val session: Session?,
        val isLoading: Boolean,
        val error: Error
    ) {
        sealed class Error {
            class FailLoadSessions(val e: Throwable) : Error()
            class FailFavorite(val e: Throwable) : Error()
            object None : Error()
            companion object {
                fun of(
                    sessionLoadState: LoadState<Session>,
                    favoriteLoadingState: LoadingState
                ): UiModel.Error {
                    if (sessionLoadState is LoadState.Error) {
                        return FailLoadSessions(sessionLoadState.e)
                    }
                    if (favoriteLoadingState is LoadingState.Error) {
                        return FailFavorite(favoriteLoadingState.e)
                    }
                    return UiModel.Error.None
                }
            }
        }

        companion object {
            val EMPTY = UiModel(null, false, Error.None)
        }
    }

    // LiveDatas
    private val sessionLoadState: LiveData<LoadState<Session>> = liveData {
        sessionRepository.sessionContents()
            .map { it.sessions.first { session -> sessionId == session.id } }
            .toLoadingState()
            .collect { loadState: LoadState<Session> ->
                emit(loadState)
            }
    }

    private val favoriteLoadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.Initialized)

    // Compose UiModel
    val uiModel: LiveData<UiModel> = composeBy(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionLoadState,
        liveData2 = favoriteLoadingState
    ) { current: UiModel,
        sessionLoadState: LoadState<Session>,
        favoriteLoadingState: LoadingState ->
        val isLoading = sessionLoadState.isLoading || favoriteLoadingState.isLoading
        val sessions = when (sessionLoadState) {
            is LoadState.Loaded -> {
                sessionLoadState.value
            }
            else -> {
                current.session
            }
        }
        UiModel(
            session = sessions,
            isLoading = isLoading,
            error = UiModel.Error.of(
                sessionLoadState = sessionLoadState,
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

    @AssistedInject.Factory
    interface Factory {
        fun create(
            sessionId: SessionId
        ): SessionDetailViewModel
    }
}