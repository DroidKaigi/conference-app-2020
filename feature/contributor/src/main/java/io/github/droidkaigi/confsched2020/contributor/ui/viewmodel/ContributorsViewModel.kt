package io.github.droidkaigi.confsched2020.contributor.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.dropWhileIndexed
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.Contributor
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.repository.ContributorRepository
import kotlinx.coroutines.launch
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
                .dropWhileIndexed { index, value ->
                    isEmptyCache(index, value)
                }
                .toLoadingState()
                .asLiveData()
        )

        try {
            contributorRepository.refresh()
        } catch (exception: Exception) {
            reloadContributorsLiveData.value = LoadingState.Error(exception)
        }
    }
    private val reloadContributorsLiveData = MutableLiveData<LoadingState>(LoadingState.Loaded)

    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = contributorsLoadStateLiveData,
        liveData2 = reloadContributorsLiveData
    ) { _, loadState, reloadState ->
        if (loadState is LoadState.Loaded) {
            contributors = loadState.value
        }
        val appError = reloadState.getErrorIfExists()?.toAppError()
            ?: loadState.getErrorIfExists()?.toAppError()
        UiModel(
            isLoading = (loadState.isLoading || reloadState.isLoading) && appError == null,
            error = appError,
            contributors = contributors
        )
    }

    fun onRetry() {
        reloadContributorsLiveData.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                contributorRepository.refresh()
                reloadContributorsLiveData.value = LoadingState.Loaded
            } catch (exception: Exception) {
                reloadContributorsLiveData.value = LoadingState.Error(exception)
            }
        }
    }

    private fun isEmptyCache(index: Int, value: List<Contributor>): Boolean =
        index == 0 && value.isEmpty()
}
