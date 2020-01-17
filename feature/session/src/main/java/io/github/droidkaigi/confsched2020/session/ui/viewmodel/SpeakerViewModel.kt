package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.model.SpeakerId
import io.github.droidkaigi.confsched2020.model.SpeechSession
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class SpeakerViewModel @AssistedInject constructor(
    @Assisted private val speakerId: SpeakerId,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    // UiModel definition
    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val speaker: Speaker?,
        val sessions: List<SpeechSession>
    ) {
        companion object {
            val EMPTY = UiModel(false, null, null, listOf())
        }
    }

    // LiveDatas
    private val speakerLoadStateLiveData: LiveData<LoadState<Speaker>> = liveData {
        sessionRepository.sessionContents()
            .map { it.speakers.first { speaker -> speakerId == speaker.id } }
            .toLoadingState()
            .collect { loadState: LoadState<Speaker> ->
                emit(loadState)
            }
    }

    private val speakerSessionLoadingStateLiveData: LiveData<LoadState<List<SpeechSession>>> = liveData {
        sessionRepository.sessionContents()
            .map { it.sessions
                .filterIsInstance<SpeechSession>()
                .filter { session -> session.speakers.firstOrNull { speaker -> speakerId == speaker.id } != null }
            }
            .toLoadingState()
            .collect { loadState: LoadState<List<SpeechSession>> ->
                emit(loadState)
            }
    }

    // Produce UiModel
    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = speakerLoadStateLiveData,
        liveData2 = speakerSessionLoadingStateLiveData
    ) { current: UiModel,
        speakerLoadState: LoadState<Speaker>,
        speakerSessionLoadState: LoadState<List<SpeechSession>> ->
        val isLoading = speakerLoadState.isLoading || speakerSessionLoadState.isLoading
        val speaker = when (speakerLoadState) {
            is LoadState.Loaded -> {
                speakerLoadState.value
            }
            else -> {
                current.speaker
            }
        }
        val speakerSessions = when (speakerSessionLoadState) {
            is LoadState.Loaded -> {
                speakerSessionLoadState.value
            }
            else -> {
                current.sessions
            }
        }
        UiModel(
            isLoading = isLoading,
            error = (speakerLoadState.getErrorIfExists()
                ?: speakerSessionLoadState.getErrorIfExists()).toAppError(),
            speaker = speaker,
            sessions = speakerSessions
        )
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            speakerId: SpeakerId
        ): SpeakerViewModel
    }
}