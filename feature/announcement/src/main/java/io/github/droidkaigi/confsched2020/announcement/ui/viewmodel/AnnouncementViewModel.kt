package io.github.droidkaigi.confsched2020.announcement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.announcement.ui.item.AnnouncementItem
import io.github.droidkaigi.confsched2020.model.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.Announcement
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState
import timber.log.Timber
import timber.log.debug

class AnnouncementViewModel @AssistedInject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val announcementItemFactory: AnnouncementItem.Factory
) : ViewModel() {
    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val announcementItems: List<AnnouncementItem>,
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
        } catch (e: Exception) {
            // We can show announcements with cache
            Timber.debug(e) { "Fail announcementRepository.refresh()" }
        }
    }

    val uiModel = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = announcementLoadStateLiveData
    ) { _, loadState ->
        val announcements = (loadState as? LoadState.Loaded)?.value.orEmpty()
        UiModel(
            isLoading = loadState.isLoading,
            error = loadState.getErrorIfExists().toAppError(),
            announcementItems = announcements.map { announcementItemFactory.create(it) },
            isEmpty = !loadState.isLoading && announcements.isEmpty()
        )
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(): AnnouncementViewModel
    }
}