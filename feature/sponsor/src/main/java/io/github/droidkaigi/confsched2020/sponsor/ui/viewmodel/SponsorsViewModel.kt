package io.github.droidkaigi.confsched2020.sponsor.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.SponsorCategory
import io.github.droidkaigi.confsched2020.model.repository.SponsorRepository
import javax.inject.Inject

class SponsorsViewModel @Inject constructor(
    private val sponsorRepository: SponsorRepository
) : ViewModel() {

    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val sponsorCategories: List<SponsorCategory>
    ) {
        companion object {
            val EMPTY = UiModel(false, null, listOf())
        }
    }

    private val sponsorsLoadStateLiveData: LiveData<LoadState<List<SponsorCategory>>> = liveData {
        emitSource(
            sponsorRepository.sponsors()
                .toLoadingState()
                .asLiveData()
        )
        try {
            sponsorRepository.refresh()
        } catch (ignored: Exception) {
            // We can show sponsors with cache
        }
    }

    val uiModel = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = sponsorsLoadStateLiveData
    ) { _, loadState ->
        val sponsorCategories = (loadState as? LoadState.Loaded)?.value.orEmpty()
        UiModel(
            isLoading = loadState.isLoading,
            error = loadState.getErrorIfExists().toAppError(),
            sponsorCategories = sponsorCategories
        )
    }
}
