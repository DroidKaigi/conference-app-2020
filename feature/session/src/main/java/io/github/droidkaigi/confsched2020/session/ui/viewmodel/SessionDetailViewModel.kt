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
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.ResultState
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.TextExpandState
import io.github.droidkaigi.confsched2020.model.ThumbsUpCount
import io.github.droidkaigi.confsched2020.model.firstErrorOrNull
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.debug

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
        val searchQuery: String?,
        val thumbsUpCount: ThumbsUpCount
    ) {
        companion object {
            val EMPTY = UiModel(
                isLoading = false,
                error = null,
                session = null,
                showEllipsis = true,
                searchQuery = null,
                thumbsUpCount = ThumbsUpCount.ZERO
            )
        }
    }

    // LiveDatas
    private val sessionLoadStateLiveData: LiveData<LoadState<Session>> = liveData {
        sessionRepository.sessionContents()
            .map {
                it.sessions.firstOrNull { session -> sessionId == session.id }
                    ?: throw AppError.ApiException.SessionNotFoundException(null)
            }
            .toLoadingState()
            .collect { loadState: LoadState<Session> ->
                emit(loadState)
            }
    }

    private val favoriteLoadingStateLiveData: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.Loaded)

    private val descriptionTextExpandStateLiveData: MutableLiveData<TextExpandState> =
        MutableLiveData(TextExpandState.COLLAPSED)

    private val totalThumbsUpCountLoadStateLiveData: LiveData<LoadState<Int>> = liveData {
        sessionRepository.thumbsUpCounts(sessionId)
            .toLoadingState()
            .collect { loadState: LoadState<Int> ->
                emit(loadState)
            }
    }

    private val incrementThumbsUpCountResultLiveData: MutableLiveData<ResultState<Int>> =
        MutableLiveData(ResultState.Success(0))

    private val incrementThumbsUpCountEvent: BroadcastChannel<Pair<SessionId, Int>> =
        BroadcastChannel(Channel.BUFFERED)

    // Produce UiModel
    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionLoadStateLiveData,
        liveData2 = favoriteLoadingStateLiveData,
        liveData3 = descriptionTextExpandStateLiveData,
        liveData4 = totalThumbsUpCountLoadStateLiveData,
        liveData5 = incrementThumbsUpCountResultLiveData
    ) { current: UiModel,
        sessionLoadState: LoadState<Session>,
        favoriteState: LoadingState,
        descriptionTextExpandState: TextExpandState,
        totalThumbsUpCountLoadState: LoadState<Int>,
        incrementThumbsUpCountResult: ResultState<Int> ->
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
        val totalThumbsUpCount = when (totalThumbsUpCountLoadState) {
            is LoadState.Loaded -> {
                totalThumbsUpCountLoadState.value
            }
            else -> {
                current.thumbsUpCount.total
            }
        }

        val incrementThumbsUpCount = incrementThumbsUpCountResult.getOrDefault(0)

        val thumbsUpCount = ThumbsUpCount(
            total = totalThumbsUpCount,
            incremented = incrementThumbsUpCount,
            incrementedUpdated = current.thumbsUpCount.incremented != incrementThumbsUpCount
        )

        val appError = listOf(
            sessionLoadState,
            favoriteState,
            totalThumbsUpCountLoadState,
            incrementThumbsUpCountResult
        )
            .firstErrorOrNull()
            .toAppError()
        UiModel(
            isLoading = isLoading,
            error = appError,
            session = sessions,
            showEllipsis = showEllipsis,
            searchQuery = searchQuery,
            thumbsUpCount = thumbsUpCount
        )
    }

    init {
        viewModelScope.launch {
            setupIncrementThumbsUpEvent()
        }
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

    fun thumbsUp(session: Session) {
        val liveDataValue = incrementThumbsUpCountResultLiveData.value
        // ResultState type cannot be use as la left operand of '?.'
        val currentIncremented = if (liveDataValue != null) {
            liveDataValue.getOrDefault(0)
        } else {
            0
        }
        val incremented = minOf(currentIncremented + 1, MAX_APPLY_COUNT)
        incrementThumbsUpCountResultLiveData.value = ResultState.Success(incremented)

        viewModelScope.launch {
            incrementThumbsUpCountEvent.send(session.id to incremented)
        }
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    private suspend fun setupIncrementThumbsUpEvent() {
        incrementThumbsUpCountEvent.asFlow()
            .debounce(INCREMENT_DEBOUNCE_MILLIS)
            .collect { (sessionId, count) ->
                try {
                    sessionRepository.incrementThumbsUpCount(
                        sessionId = sessionId,
                        count = count
                    )
                    Timber.debug { "increment thumbs-up: $count posted" }
                    // Return initial value
                    incrementThumbsUpCountResultLiveData.value = ResultState.Success(0)
                } catch (e: Exception) {
                    Timber.debug { "increment thumbs-up error: $e" }
                    incrementThumbsUpCountResultLiveData.value = ResultState.Error(e)
                }
            }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            sessionId: SessionId,
            searchQuery: String? = null
        ): SessionDetailViewModel
    }

    companion object {
        private const val INCREMENT_DEBOUNCE_MILLIS = 1000L
        private const val MAX_APPLY_COUNT = 50
    }
}
