package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.work.WorkInfo
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.FavoriteToggleWorkerManager
import io.github.droidkaigi.confsched2020.ext.composeBy
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class SessionDetailViewModel @AssistedInject constructor(
    @Assisted private val sessionId: SessionId,
    private val sessionRepository: SessionRepository,
    private val favoriteToggleWorkerManager: FavoriteToggleWorkerManager
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

    private val favoriteLoadingStateLiveData: LiveData<WorkInfo.State> =
        favoriteToggleWorkerManager.liveData()

    // Compose UiModel
    val uiModel: LiveData<UiModel> = composeBy(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionLoadStateLiveData,
        liveData2 = favoriteLoadingStateLiveData
    ) { current: UiModel,
        sessionLoadState: LoadState<Session>,
        favoriteState: WorkInfo.State ->
        val isLoading =
            sessionLoadState.isLoading || !favoriteState.isFinished
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
            error = sessionLoadState
                .getErrorIfExists()
                .toAppError()
                ?: favoriteState.toAppError()
            ,
            session = sessions
        )
    }

    fun favorite(session: Session) {
        favoriteToggleWorkerManager.start(session.id)
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            sessionId: SessionId
        ): SessionDetailViewModel
    }
}