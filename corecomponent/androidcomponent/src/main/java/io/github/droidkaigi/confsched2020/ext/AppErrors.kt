package io.github.droidkaigi.confsched2020.ext

import androidx.annotation.StringRes
import com.google.firebase.firestore.FirebaseFirestoreException
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.widget.component.R
import io.ktor.client.features.ResponseException
import io.ktor.util.cio.ChannelReadException
import kotlinx.coroutines.TimeoutCancellationException

fun Throwable?.toAppError(): AppError? {
    return when (this) {
        null -> null
        is AppError -> this
        is ResponseException ->
            return AppError.ApiException.ServerException(this)
        is ChannelReadException ->
            return AppError.ApiException.NetworkException(this)
        is FirebaseFirestoreException ->
            when (code) {
                FirebaseFirestoreException.Code.UNAVAILABLE ->
                    AppError.ApiException.NetworkException(this)
                else -> AppError.ApiException.UnknownException(this)
            }
        is TimeoutCancellationException -> AppError.ApiException.NetworkException(this)
        else -> AppError.UnknownException(this)
    }
}

@StringRes
fun AppError.stringRes() = when (this) {
    is AppError.ApiException.NetworkException -> R.string.error_network
    is AppError.ApiException.ServerException -> R.string.error_server
    is AppError.ApiException.SessionNotFoundException -> R.string.error_unknown
    is AppError.ApiException.UnknownException -> R.string.error_unknown
    is AppError.ExternalIntegrationError.NoCalendarIntegrationFoundException
        -> R.string.error_no_calendar_integration
    is AppError.UnknownException -> R.string.error_unknown
}
