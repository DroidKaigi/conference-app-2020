package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.TextExpandState
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SessionDetailViewModel @AssistedInject constructor(
    @Assisted private val sessionId: SessionId,
    @Assisted private val searchQuery: String?,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    // UiModel definition
    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val session: Session?,
        val showEllipsis: Boolean,
        val searchQuery: String?
    ) {
        companion object {
            val EMPTY = UiModel(false, null, null, true, null)
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
        MutableLiveData(LoadingState.Loaded)

    private val descriptionTextExpandStateLiveData: MutableLiveData<TextExpandState> =
        MutableLiveData(TextExpandState.COLLAPSED)

    // Produce UiModel
    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionLoadStateLiveData,
        liveData2 = favoriteLoadingStateLiveData,
        liveData3 = descriptionTextExpandStateLiveData
    ) { current: UiModel,
        sessionLoadState: LoadState<Session>,
        favoriteState: LoadingState,
        descriptionTextExpandState: TextExpandState ->
        val isLoading =
            sessionLoadState.isLoading || favoriteState.isLoading
        val sessions = when (sessionLoadState) {
            is LoadState.Loaded -> {
                sessionLoadState.value
            }
            else -> {
                current.session
            }
        }
        val showEllipsis = descriptionTextExpandState == TextExpandState.COLLAPSED

        UiModel(
            isLoading = isLoading,
            error = sessionLoadState
                .getErrorIfExists()
                .toAppError()
                ?: favoriteState
                    .getErrorIfExists()
                    .toAppError(),
            session = sessions,
            showEllipsis = showEllipsis,
            searchQuery = searchQuery
        )
    }

    fun favorite(session: Session) {
        viewModelScope.launch {
            favoriteLoadingStateLiveData.value = LoadingState.Loading
            try {
                sessionRepository.toggleFavoriteWithWorker(session.id)
                favoriteLoadingStateLiveData.value = LoadingState.Loaded
            } catch (e: Exception) {
                favoriteLoadingStateLiveData.value = LoadingState.Error(e)
            }
        }
    }

    fun expandDescription() {
        descriptionTextExpandStateLiveData.value = TextExpandState.EXPANDED
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            sessionId: SessionId,
            searchQuery: String? = null
        ): SessionDetailViewModel
    }
}
