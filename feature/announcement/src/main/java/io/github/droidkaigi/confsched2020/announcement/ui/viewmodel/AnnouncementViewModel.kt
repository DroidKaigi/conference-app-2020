package io.github.droidkaigi.confsched2020.announcement.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.AnnouncementRepository

class AnnouncementViewModel @AssistedInject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(): AnnouncementViewModel
    }
}