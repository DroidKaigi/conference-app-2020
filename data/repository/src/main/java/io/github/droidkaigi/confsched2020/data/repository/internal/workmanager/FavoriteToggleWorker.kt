package io.github.droidkaigi.confsched2020.data.repository.internal.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.droidkaigi.confsched2020.di.AppComponentHolder
import io.github.droidkaigi.confsched2020.model.SessionId
import kotlinx.coroutines.withTimeout

internal class FavoriteToggleWorker(
    private val appContext: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        if (runAttemptCount > 0) {
            return Result.failure()
        }
        val appComponentHolder = appContext as? AppComponentHolder ?: return Result.failure()
        val sessionRepository = appComponentHolder.appComponent.sessionRepository()
        val id = workerParams.inputData.getString(INPUT_SESSION_ID_KEY)
        id ?: return Result.failure()
        withTimeout(3000) {
            sessionRepository.toggleFavorite(SessionId(id))
        }
        return Result.success()
    }

    companion object {
        const val INPUT_SESSION_ID_KEY = "INPUT_SESSION_ID_KEY"
    }
}
