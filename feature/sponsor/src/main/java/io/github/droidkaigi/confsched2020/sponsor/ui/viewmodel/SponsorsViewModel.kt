package io.github.droidkaigi.confsched2020.sponsor.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.SponsorRepository
import io.github.droidkaigi.confsched2020.ext.asLiveData
import io.github.droidkaigi.confsched2020.ext.composeBy
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.Filters
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.SponsorCategory

class SponsorsViewModel @AssistedInject constructor(
    val sponsorRepository: SponsorRepository
) : ViewModel() {
    // UiModel definition
    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val filters: Filters,
        val sponsors: List<SponsorCategory>
    ) {
        companion object {
            val EMPTY = UiModel(false, null, Filters(), listOf())
        }
    }

    // LiveDatas
    private val sponsorsLoadStateLiveData: LiveData<LoadState<List<SponsorCategory>>> = liveData {
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

    private val filterLiveData: MutableLiveData<Filters> = MutableLiveData(Filters())

    // Compose UiModel
    val uiModel: LiveData<UiModel> = composeBy(
        initialValue = UiModel.EMPTY,
        liveData1 = sponsorsLoadStateLiveData,
        liveData2 = filterLiveData
    ) { current: UiModel,
        sponsorsLoadState: LoadState<List<SponsorCategory>>,
        filters: Filters
        ->
        val isLoading = sponsorsLoadState.isLoading
        val sponsorContents = when (sponsorsLoadState) {
            is LoadState.Loaded -> {
                sponsorsLoadState.value
            }
            else -> {
                listOf()
            }
        }
        UiModel(
            isLoading = isLoading,
            error = sponsorsLoadState.getExceptionIfExists().toAppError(),
            filters = filters,
            sponsors = sponsorContents
        )
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(): SponsorsViewModel
    }
}