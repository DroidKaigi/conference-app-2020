package io.github.droidkaigi.confsched2020.model

// FIXME: suppport more exceptions
sealed class AppError : RuntimeException {
    constructor()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)

    sealed class ApiException(cause: Throwable?) : AppError(cause) {
        class NetworkException(cause: Throwable?) : ApiException(cause)
        class ServerException(cause: Throwable?) : ApiException(cause)
        class UnknownException(cause: Throwable?) : AppError(cause)
    }

    class UnknownException(cause: Throwable?) : AppError(cause)
}
