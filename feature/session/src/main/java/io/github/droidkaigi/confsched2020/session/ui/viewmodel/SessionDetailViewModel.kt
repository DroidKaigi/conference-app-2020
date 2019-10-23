package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.annotation.CheckResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.ext.LifecycleRunnable
import io.github.droidkaigi.confsched2020.ext.composeBy
import io.github.droidkaigi.confsched2020.ext.onChanged
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
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
        val isLoading: Boolean,
        val error: AppError?,
        val session: Session?
    ) {
        companion object {
            val EMPTY = UiModel(false, null, null)
        }
    }

    // LiveDatas
    private val sessionLoadStateLiveData: LiveData<LoadState<Session>> = liveData {
        sessionRepository.sessionContents()
            .map { it.sessions.first { session -> sessionId == session.id } }
            .toLoadingState()
            .collect { loadState: LoadState<Session> ->
                emit(loadState)
            }
    }

    private val favoriteLoadingStateLiveData: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.Initialized)

    // Compose UiModel
    val uiModel: LiveData<UiModel> = composeBy(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionLoadStateLiveData,
        liveData2 = favoriteLoadingStateLiveData
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
            isLoading = isLoading,
            error = (sessionLoadState.getExceptionIfExists()
                ?: favoriteLoadingState.getExceptionIfExists()).toAppError(),
            session = sessions
        )
    }

    // Functions
    @CheckResult
    fun favorite(session: Session): LifecycleRunnable {
        return liveData {
            try {
                emit(LoadingState.Loading)
                sessionRepository.toggleFavorite(session)
                emit(LoadingState.Loaded)
            } catch (e: Exception) {
                emit(LoadingState.Error(e))
            }
        }.onChanged {
            favoriteLoadingStateLiveData.value = it
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            sessionId: SessionId
        ): SessionDetailViewModel
    }
}