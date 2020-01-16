package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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
import io.github.droidkaigi.confsched2020.model.SessionPage
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.debug
import javax.inject.Inject

class SessionsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {
    // UiModel definition
    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val dayToSessionsMap: Map<SessionPage.Day, List<Session>>,
        val favoritedSessions: List<Session>,
        val filters: Filters,
        val allFilters: Filters
    ) {
        companion object {
            val EMPTY = UiModel(true, null, mapOf(), listOf(), Filters(), Filters())
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
    private var favoriteLoadingStateLiveData: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.Loaded)

    private val filterLiveData: MutableLiveData<Filters> = MutableLiveData(Filters())

    // map of the given Day and the current ongoing sessions index in the list of that day
    val currentSessionMap = MutableLiveData<Map<SessionPage.Day, Int>>()

    // Produce UiModel
    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionsLoadStateLiveData,
        liveData2 = favoriteLoadingStateLiveData,
        liveData3 = filterLiveData
    ) { current: UiModel,
        sessionsLoadState: LoadState<SessionContents>,
        favoriteState: LoadingState,
        filters: Filters
        ->
        val isLoading = sessionsLoadState.isLoading || favoriteState.isLoading
        val sessionContents = when (sessionsLoadState) {
            is LoadState.Loaded -> {
                sessionsLoadState.value
            }
            else -> {
                SessionContents.EMPTY
            }
        }
        val filteredSessions = sessionContents
            .sessions
            .filter { filters.isPass(it) }

        // map of given Day and list of sessions associated with that day
        val groupedSessions = filteredSessions.groupBy { it.dayNumber }

        /*
            Since the very first loading of sessions will set current value
            as UiModel.EMPTY, that is how we identify the first load. Subsequent
            calls will never set current to UiModel.EMPTY
         */
        val isFirstCall = (current == UiModel.EMPTY)

        // if first time load, we set value to currentSessionMap
        if(isFirstCall) {
            this.currentSessionMap.value = mapOf(
                Pair(SessionPage.Day1, groupedSessions[SessionPage.Day1.day]?.indexOfFirst { it.isOnGoing } ?: -1),
                Pair(SessionPage.Day2, groupedSessions[SessionPage.Day2.day]?.indexOfFirst { it.isOnGoing } ?: -1)
            )
        }

        UiModel(
            isLoading = isLoading,
            error = sessionsLoadState
                .getErrorIfExists()
                .toAppError() ?: favoriteState
                .getErrorIfExists()
                .toAppError(),
            dayToSessionsMap = groupedSessions
                .mapKeys {
                    SessionPage.dayOfNumber(
                        it.key
                    )
                },
            favoritedSessions = filteredSessions
                .filter { it.isFavorited },
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
            categories = if (checked) filters.categories + category else filters.categories - category
        )
    }

    fun filterChanged(lang: Lang, checked: Boolean) {
        val filters = filterLiveData.requireValue()
        filterLiveData.value = filters.copy(
            langs = if (checked) filters.langs + lang else filters.langs - lang
        )
    }

    fun filterChanged(langSupport: LangSupport, checked: Boolean) {
        val filters = filterLiveData.requireValue()
        filterLiveData.value = filters.copy(
            langSupports = if (checked) filters.langSupports + langSupport else filters.langSupports - langSupport
        )
    }

    fun filterChanged(audienceCategory: AudienceCategory, checked: Boolean) {
        val filters = filterLiveData.requireValue()
        filterLiveData.value = filters.copy(
            audienceCategories = if (checked) filters.audienceCategories + audienceCategory else filters.audienceCategories - audienceCategory
        )
    }

    fun resetFilter() {
        filterLiveData.value = Filters()
    }
}
