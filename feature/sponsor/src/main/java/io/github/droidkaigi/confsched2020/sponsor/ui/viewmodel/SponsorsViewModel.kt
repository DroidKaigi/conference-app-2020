package io.github.droidkaigi.confsched2020.sponsor.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.SponsorRepository
import io.github.droidkaigi.confsched2020.ext.asLiveData
import io.github.droidkaigi.confsched2020.ext.composeBy
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.SponsorPlan

class SponsorsViewModel @AssistedInject constructor(
    private val sponsorRepository: SponsorRepository
) : ViewModel() {

    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val sponsorPlans: List<SponsorPlan>
    ) {
        companion object {
            val EMPTY = UiModel(false, null, listOf())
        }
    }

    private val sponsorsLoadStateLiveData: LiveData<LoadState<List<SponsorPlan>>> = liveData {
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

    val uiModel = composeBy(
        initialValue = UiModel.EMPTY,
        liveData1 = sponsorsLoadStateLiveData
    ) { _, loadState ->
        val sponsorPlans = (loadState as? LoadState.Loaded)?.value.orEmpty()
        UiModel(
            isLoading = loadState.isLoading,
            error = loadState.getErrorIfExists().toAppError(),
            sponsorPlans = sponsorPlans
        )
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(): SponsorsViewModel
    }
}