package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionId
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class SessionDetailViewModel @AssistedInject constructor(
    @Assisted private val state: SavedStateHandle,
    @Assisted private val sessionId: SessionId,
    val sessionRepository: SessionRepository
) : ViewModel() {

    val sessionLoadingState = liveData {
        sessionRepository.sessionContents()
            .map { it.sessions.firstOrNull { session -> sessionId == session.id } }
            .toLoadingState()
            .collect { loadingState: LoadingState<Session?> ->
                emit(loadingState)
            }
    }

    fun favorite(session: Session): LiveData<Unit> {
        return liveData {
            sessionRepository.toggleFavorite(session)
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            state: SavedStateHandle,
            sessionId: SessionId
        ): SessionDetailViewModel
    }
}