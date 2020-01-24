package io.github.droidkaigi.confsched2020.session_survey.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.SessionId

class SessionSurveyViewModel @AssistedInject constructor(
    @Assisted private val sessionId: SessionId
) : ViewModel() {

    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?
    ) {
        companion object {
            val EMPTY = UiModel(false, null)
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(sessionId: SessionId): SessionSurveyViewModel
    }
}
