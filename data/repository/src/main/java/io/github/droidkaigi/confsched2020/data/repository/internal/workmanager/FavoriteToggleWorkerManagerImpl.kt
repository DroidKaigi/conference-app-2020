package io.github.droidkaigi.confsched2020.data.repository.internal.workmanager

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.github.droidkaigi.confsched2020.api.BuildConfig
import io.github.droidkaigi.confsched2020.data.repository.FavoriteToggleWorkerManager
import io.github.droidkaigi.confsched2020.model.SessionId
import javax.inject.Inject

internal class FavoriteToggleWorkerManagerImpl @Inject constructor(
    private val context: Context
) : FavoriteToggleWorkerManager {
    override fun start(sessionId: SessionId) {
        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(
                FAVORITE_WORKER_TAG,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<FavoriteToggleWorker>()
                    .addTag(FAVORITE_WORKER_TAG)
                    .setInputData(
                        Data
                            .Builder()
                            .putString(FavoriteToggleWorker.INPUT_SESSION_ID_KEY, sessionId.id)
                            .build()
                    )
                    .build()
            )
    }

    override fun liveData(): LiveData<WorkInfo.State> {
        return WorkManager.getInstance(context.applicationContext)
            .getWorkInfosByTagLiveData(FAVORITE_WORKER_TAG)
            .map {
                check(!(BuildConfig.DEBUG && it.count() > 1)) { "Must not have one more work info" }
                it.firstOrNull()?.state ?: WorkInfo.State.SUCCEEDED
            }
    }

    companion object {
        private const val FAVORITE_WORKER_TAG = "FAVORITE_WORKER_TAG"
    }
}