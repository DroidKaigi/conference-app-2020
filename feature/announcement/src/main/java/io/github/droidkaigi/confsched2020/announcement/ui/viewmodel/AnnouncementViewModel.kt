package io.github.droidkaigi.confsched2020.announcement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2020.ext.asLiveData
import io.github.droidkaigi.confsched2020.ext.composeBy
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.Announcement
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState

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

    private val announcementLoadStateLiveData: LiveData<LoadState<List<Announcement>>> = liveData {
        emitSource(
            announcementRepository.announcements()
                .toLoadingState()
                .asLiveData()
        )
        try {
            announcementRepository.refresh()
        } catch (ignored: Exception) {
            // TODO: Show from cache?
        }
    }

    val uiModel = composeBy(
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

    @AssistedInject.Factory
    interface Factory {
        fun create(): AnnouncementViewModel
    }
}