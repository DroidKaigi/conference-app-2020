package io.github.droidkaigi.confsched2020.model

interface ErrorGettable {
    fun getErrorIfExists(): Throwable?
}

sealed class LoadState<out T> : ErrorGettable {
    object Loading : LoadState<Nothing>()
    class Loaded<T>(val value: T) : LoadState<T>()
    class Error<T>(val e: Throwable) : LoadState<T>()

    val isLoading get() = this is Loading
    override fun getErrorIfExists() = if (this is Error) e else null
    fun getValueOrNull(): T? = if (this is Loaded<T>) value else null
}

sealed class LoadingState : ErrorGettable {
    object Loading : LoadingState()
    object Loaded : LoadingState()
    class Error(val e: Throwable) : LoadingState()

    val isLoading get() = this is Loading
    override fun getErrorIfExists() = if (this is Error) e else null
}

sealed class ResultState<T> : ErrorGettable {
    class Success<T>(val value: T) : ResultState<T>()
    class Error<T>(val e: Throwable) : ResultState<T>()

    override fun getErrorIfExists() = if (this is Error) e else null
    fun getOrDefault(default: T): T {
        if (this is Success) return value
        return default
    }
}

fun List<ErrorGettable>.firstErrorOrNull(): Throwable? {
    return mapNotNull { it.getErrorIfExists() }.firstOrNull()
}
