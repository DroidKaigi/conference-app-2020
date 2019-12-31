package io.github.droidkaigi.confsched2020.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import io.github.droidkaigi.confsched2020.model.SessionId

interface FavoriteToggleWorkerManager {
    fun start(sessionId: SessionId)
    fun liveData(): LiveData<WorkInfo.State>
}