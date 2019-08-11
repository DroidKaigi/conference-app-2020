package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.model.SessionContents
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class SessionViewModel @Inject constructor(sessionRepository: SessionRepository) : ViewModel() {
    val sessionContents = liveData {
        supervisorScope {
            val deferred = async { sessionRepository.refresh() }
            val flow: Flow<SessionContents> = sessionRepository.sessionContents()
            flow.collect { sessionContents ->
                emit(sessionContents)
            }
            deferred.await()
        }
    }
}
