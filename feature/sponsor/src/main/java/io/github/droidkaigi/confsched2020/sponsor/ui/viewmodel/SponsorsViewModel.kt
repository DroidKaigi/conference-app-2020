package io.github.droidkaigi.confsched2020.sponsor.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.SponsorRepository
import io.github.droidkaigi.confsched2020.ext.asLiveData
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.SponsorCategory

class SponsorsViewModel @AssistedInject constructor(
    private val sponsorRepository: SponsorRepository
) : ViewModel() {

    val sponsorsLoadStateLiveData: LiveData<LoadState<List<SponsorCategory>>> = liveData {
        emitSource(
            sponsorRepository.sponsors()
                .toLoadingState()
                .asLiveData()
        )
        try {
            sponsorRepository.refresh()
        } catch (ignored: Exception) {
            // We can show sessions with cache
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(): SponsorsViewModel
    }
}