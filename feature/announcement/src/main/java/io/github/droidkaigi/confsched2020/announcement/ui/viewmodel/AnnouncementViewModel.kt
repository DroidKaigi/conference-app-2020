package io.github.droidkaigi.confsched2020.announcement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2020.ext.asLiveData
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.Announcement
import io.github.droidkaigi.confsched2020.model.LoadState

class AnnouncementViewModel @AssistedInject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    val announcementLoadStateLiveData: LiveData<LoadState<List<Announcement>>> = liveData {
        emitSource(
            announcementRepository.announcements()
                .toLoadingState()
                .asLiveData()
        )
        try {
            announcementRepository.refresh()
        } catch (ignored: Exception) {
            // NOP
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(): AnnouncementViewModel
    }
}