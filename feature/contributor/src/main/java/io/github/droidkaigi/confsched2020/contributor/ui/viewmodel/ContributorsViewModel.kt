package io.github.droidkaigi.confsched2020.contributor.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.Contributor
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.repository.ContributorRepository
import java.lang.Exception
import javax.inject.Inject

class ContributorsViewModel @Inject constructor(
    private val contributorRepository: ContributorRepository
) : ViewModel() {

    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val contributors: List<Contributor>
    ) {
        companion object {
            val EMPTY = UiModel(false, null, emptyList())
        }
    }

    private var contributors: List<Contributor> = emptyList()

    private val contributorsLoadStateLiveData: LiveData<LoadState<List<Contributor>>> = liveData {
        emitSource(
            contributorRepository.contributorContents()
                .toLoadingState()
                .asLiveData()
        )

        try {
            contributorRepository.refresh()
        } catch (exception: Exception) {
            // We can show contributors with cache
        }
    }

    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = contributorsLoadStateLiveData
    ) { _, loadState ->
        if (loadState is LoadState.Loaded) {
            contributors = loadState.value
        }
        UiModel(
            isLoading = loadState.isLoading,
            error = loadState.getErrorIfExists()?.toAppError(),
            contributors = contributors
        )
    }
}
