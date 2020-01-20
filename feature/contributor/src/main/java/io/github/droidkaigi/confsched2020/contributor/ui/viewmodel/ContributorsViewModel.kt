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

    /**
     * [ViewState] has to be wrapped to workaround a compile error about type mismatch.
     * [combine] doesn't seem to understand child classes.
     */
    data class UiModel(val viewState: ViewState) {
        companion object {
            val DEFAULT = UiModel(ViewState.Loading)
        }
    }

    sealed class ViewState {
        object Loading : ViewState()

        data class Loaded(val contributors: List<Contributor>) : ViewState()

        data class Error(val error: AppError?) : ViewState()
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
            // Emit error so that uiModel knows when it's done loading
            emit(toErrorLoadState(exception))
        }
    }

    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.DEFAULT,
        liveData1 = contributorsLoadStateLiveData
    ) { _, loadState ->
        when (loadState) {
            is LoadState.Loading -> UiModel(ViewState.Loading)
            is LoadState.Error -> {
                val appError = loadState.getErrorIfExists()?.toAppError()
                if (contributors.isNotEmpty()) {
                    UiModel(ViewState.Loaded(contributors))
                } else {
                    UiModel(ViewState.Error(appError))
                }
            }
            is LoadState.Loaded -> {
                contributors = loadState.value
                if (loadState.value.isNotEmpty()) {
                    UiModel(ViewState.Loaded(loadState.value))
                } else {
                    // Continue treating as loading until data is fetched or error'ed.
                    // e.g. the first time app is installed when there's no cache yet,
                    // repository returns an empty list
                    UiModel(ViewState.Loading)
                }
            }
        }
    }

    // Had to make this a method so that the compiler doesn't complain about the type
    private fun toErrorLoadState(exception: Exception): LoadState<List<Contributor>> =
        LoadState.Error(exception)
}
