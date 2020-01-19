package io.github.droidkaigi.confsched2020.announcement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.Announcement
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.repository.AnnouncementRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.debug

class AnnouncementViewModel @AssistedInject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val announcements: List<Announcement>,
        val isEmpty: Boolean
    ) {
        companion object {
            val EMPTY = UiModel(false, null, listOf(), false)
        }
    }

    private val mutableAnnouncementLoadStateLiveData: MutableLiveData<LoadState<List<Announcement>>> =
        MutableLiveData()
    private val announcementLoadStateLiveData: LiveData<LoadState<List<Announcement>>>
        get() = mutableAnnouncementLoadStateLiveData

    val uiModel = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = announcementLoadStateLiveData
    ) { _, loadState ->
        val announcements = (loadState as? LoadState.Loaded)?.value.orEmpty()
        UiModel(
            isLoading = loadState.isLoading,
            error = loadState.getErrorIfExists().toAppError(),
            announcements = announcements,
            isEmpty = !loadState.isLoading && announcements.isEmpty()
        )
    }

    fun load() {
        viewModelScope.launch {
            try {
                announcementRepository.refresh()
            } catch (e: Exception) {
                // We can show announcements with cache
                Timber.debug(e) { "Fail announcementRepository.refresh()" }
            }
            announcementRepository.announcements()
                .toLoadingState()
                .collect {
                    mutableAnnouncementLoadStateLiveData.value = it
                }
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(): AnnouncementViewModel
    }
}