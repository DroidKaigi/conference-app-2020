package io.github.droidkaigi.confsched2020.data.repository.internal.workmanager

import android.content.Context
import androidx.core.os.OperationCanceledException
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.SessionId
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

internal class FavoriteToggleWork @Inject constructor(
    context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    suspend fun start(sessionId: SessionId) {
        val workRequest = OneTimeWorkRequestBuilder<FavoriteToggleWorker>()
            .addTag(FAVORITE_WORKER_TAG)
            .setInputData(
                Data
                    .Builder()
                    .putString(FavoriteToggleWorker.INPUT_SESSION_ID_KEY, sessionId.id)
                    .build()
            )
            .build()
        workManager
            .enqueueUniqueWork(
                FAVORITE_WORKER_TAG,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
            .await()
        suspendCancellableCoroutine<Unit> { cancellableContinuation ->
            try {
                val workInfoByIdLiveData = workManager.getWorkInfoByIdLiveData(workRequest.id)
                val observer = object : Observer<WorkInfo> {
                    override fun onChanged(workInfo: WorkInfo?) {
                        val state = workInfo?.state ?: return
                        if (state == WorkInfo.State.SUCCEEDED) {
                            cancellableContinuation.resume(Unit)
                            workInfoByIdLiveData.removeObserver(this)
                            return
                        }
                        if (state.isFinished) {
                            cancellableContinuation.resumeWithException(
                                AppError.ApiException.NetworkException(
                                    OperationCanceledException()
                                )
                            )
                            workInfoByIdLiveData.removeObserver(this)
                        }
                    }
                }
                workInfoByIdLiveData
                    .observeForever(observer)

                cancellableContinuation.invokeOnCancellation {
                    workInfoByIdLiveData.removeObserver(observer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val FAVORITE_WORKER_TAG = "FAVORITE_WORKER_TAG"
    }
}
