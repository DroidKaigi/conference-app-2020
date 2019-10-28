package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.ext.asLiveData
import io.github.droidkaigi.confsched2020.ext.composeBy
import io.github.droidkaigi.confsched2020.ext.setOnEach
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.Filters
import io.github.droidkaigi.confsched2020.model.Lang
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionPage
import timber.log.Timber
import timber.log.debug
import javax.inject.Inject

class SessionsViewModel @Inject constructor(
    val sessionRepository: SessionRepository
) : ViewModel() {
    // UiModel definition
    data class UiModel(
        val sessionContents: SessionContents?,
        val filters: Filters,
        val dayToSessions: Map<SessionPage.Day, List<Session>>,
        val isLoading: Boolean,
        val error: AppError?
    ) {
        companion object {
            val EMPTY = UiModel(null, Filters(), mapOf(), false, null)
        }
    }

    // LiveDatas
    private val sessionsLoadStateLiveData: LiveData<LoadState<SessionContents>> = liveData {
        emitSource(
            sessionRepository.sessionContents()
                .toLoadingState()
                .asLiveData()
        )
        try {
            sessionRepository.refresh()
        } catch (ignored: Exception) {
            // We can show sessions with cache
        }
    }
    private val favoriteLoadingStateLiveData: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.Initialized)

    private val filterLiveData: MutableLiveData<Filters> = MutableLiveData(Filters())

    // Compose UiModel
    val uiModel: LiveData<UiModel> = composeBy(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionsLoadStateLiveData,
        liveData2 = favoriteLoadingStateLiveData,
        liveData3 = filterLiveData
    ) { current: UiModel,
        sessionsLoadState: LoadState<SessionContents>,
        favoriteLoadingState: LoadingState,
        filters: Filters
        ->
        Timber.debug { "sessionsLoadState:" + sessionsLoadState + " favoriteLoadingState:" + favoriteLoadingState }
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
            dayToSessions = sessionContents
                ?.sessions
                .orEmpty()
                .filter { filters.isPass(it) }
                .groupBy { it.dayNumber }
                .mapKeys {
                    SessionPage.dayOfNumber(
                        it.key
                    )
                },
            filters = filters,
            isLoading = isLoading,
            error = (sessionsLoadState.getExceptionIfExists()
                ?: favoriteLoadingState.getExceptionIfExists()).toAppError()
        )
    }

    // Functions
    fun favorite(session: Session): LiveData<LoadingState> {
        return liveData {
            try {
                emit(LoadingState.Loading)
                sessionRepository.toggleFavorite(session)
                emit(LoadingState.Loaded)
            } catch (e: Exception) {
                emit(LoadingState.Error(e))
            }
        }.setOnEach(favoriteLoadingStateLiveData)
    }

    fun onFilterIsOnlyEnglishChanged(isOnlyEnglish: Boolean) {
        filterLiveData.value = filterLiveData.value?.copy(
            langs = if (isOnlyEnglish) setOf(Lang.EN) else setOf()
        )
    }
}
