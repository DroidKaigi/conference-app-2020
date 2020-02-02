package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.requireValue
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.AudienceCategory
import io.github.droidkaigi.confsched2020.model.Category
import io.github.droidkaigi.confsched2020.model.Filters
import io.github.droidkaigi.confsched2020.model.Lang
import io.github.droidkaigi.confsched2020.model.LangSupport
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.Room
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionList
import io.github.droidkaigi.confsched2020.model.SessionPage
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.session.util.SessionAlarm
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.debug
import javax.inject.Inject

class SessionsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val sessionAlarm: SessionAlarm
) : ViewModel() {
    // UiModel definition
    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val dayToSessionsMap: Map<SessionPage.Day, SessionList>,
        val shouldScrollSessionPosition: Map<SessionPage, Int>,
        val events: SessionList,
        val favoritedSessions: SessionList,
        val filters: Filters,
        val allFilters: Filters
    ) {
        companion object {
            val EMPTY = UiModel(
                true,
                null,
                mapOf(),
                mapOf(),
                SessionList.EMPTY,
                SessionList.EMPTY,
                Filters(),
                Filters()
            )
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
        } catch (e: Exception) {
            // We can show sessions with cache
            Timber.debug(e) { "Fail sessionRepository.refresh()" }
        }
    }
    private var favoriteLoadingStateLiveData: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.Loaded)

    private val filterLiveData: MutableLiveData<Filters> = MutableLiveData(Filters())

    private val shouldScrollCurrentSessionLiveData = MutableLiveData(true)

    // Produce UiModel
    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionsLoadStateLiveData,
        liveData2 = favoriteLoadingStateLiveData,
        liveData3 = filterLiveData,
        liveData4 = shouldScrollCurrentSessionLiveData
    ) { current: UiModel,
        sessionsLoadState: LoadState<SessionContents>,
        favoriteState: LoadingState,
        filters: Filters,
        shouldScroll: Boolean
        ->
        val isLoading = sessionsLoadState.isLoading || favoriteState.isLoading
        val sessionContents = sessionsLoadState.getValueOrNull() ?: SessionContents.EMPTY

        val sessions = sessionContents.sessions
        val filteredSessions = sessions.filtered(filters)
        val dayToSessionMap = filteredSessions.dayToSessionMap
        val shouldScrollSessionPosition =
            if (shouldScroll) filteredSessions.toPageToScrollPositionMap() else emptyMap()

        UiModel(
            isLoading = isLoading,
            error = sessionsLoadState
                .getErrorIfExists()
                .toAppError() ?: favoriteState
                .getErrorIfExists()
                .toAppError(),
            dayToSessionsMap = dayToSessionMap,
            shouldScrollSessionPosition = shouldScrollSessionPosition,
            events = sessions.events,
            favoritedSessions = filteredSessions.favorited,
            filters = filters,
            allFilters = Filters(
                rooms = sessionContents.rooms.toSet(),
                audienceCategories = sessionContents.audienceCategories.toSet(),
                categories = sessionContents.category.toSet(),
                langs = sessionContents.langs.toSet(),
                langSupports = sessionContents.langSupports.toSet()
            )
        )
    }

    // Functions
    fun favorite(session: Session) {
        viewModelScope.launch {
            favoriteLoadingStateLiveData.value = LoadingState.Loading
            try {
                sessionRepository.toggleFavoriteWithWorker(session.id)
                sessionAlarm.toggleRegister(session)
                favoriteLoadingStateLiveData.value = LoadingState.Loaded
            } catch (e: Exception) {
                favoriteLoadingStateLiveData.value = LoadingState.Error(e)
            }
        }
    }

    fun filterChanged(room: Room, checked: Boolean) {
        val filters = filterLiveData.requireValue()
        filterLiveData.value = filters.copy(
            rooms = if (checked) filters.rooms + room else filters.rooms - room
        )
    }

    fun filterChanged(category: Category, checked: Boolean) {
        val filters = filterLiveData.requireValue()
        filterLiveData.value = filters.copy(
            categories = if (checked) {
                filters.categories + category
            } else {
                filters.categories - category
            }
        )
    }

    fun filterChanged(lang: Lang, checked: Boolean) {
        val filters = filterLiveData.requireValue()
        filterLiveData.value = filters.copy(
            langs = if (checked) {
                filters.langs + lang
            } else {
                filters.langs - lang
            }
        )
    }

    fun filterChanged(langSupport: LangSupport, checked: Boolean) {
        val filters = filterLiveData.requireValue()
        filterLiveData.value = filters.copy(
            langSupports = if (checked) {
                filters.langSupports + langSupport
            } else {
                filters.langSupports - langSupport
            }
        )
    }

    fun filterChanged(audienceCategory: AudienceCategory, checked: Boolean) {
        val filters = filterLiveData.requireValue()
        filterLiveData.value = filters.copy(
            audienceCategories = if (checked) {
                filters.audienceCategories + audienceCategory
            } else {
                filters.audienceCategories - audienceCategory
            }
        )
    }

    fun resetFilter() {
        filterLiveData.value = Filters()
    }

    fun onScrolled() {
        shouldScrollCurrentSessionLiveData.value = false
    }

    fun onTabReselected() {
        shouldScrollCurrentSessionLiveData.value = true
    }
}
