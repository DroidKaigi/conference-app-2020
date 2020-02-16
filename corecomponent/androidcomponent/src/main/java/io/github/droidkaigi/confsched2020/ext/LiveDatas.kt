package io.github.droidkaigi.confsched2020.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.hadilq.liveevent.LiveEvent
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.ErrorGettable

fun <T : Any> LiveData<T>.requireValue() = requireNotNull(value)

inline fun <T : Any, LIVE1 : Any> combine(
    initialValue: T,
    liveData1: LiveData<LIVE1>,
    crossinline block: (T, LIVE1) -> T
): LiveData<T> {
    return MediatorLiveData<T>().apply {
        value = initialValue
        listOf(liveData1).forEach { liveData ->
            addSource(liveData) {
                val currentValue = value
                val liveData1Value = liveData1.value
                if (currentValue != null && liveData1Value != null) {
                    value = block(currentValue, liveData1Value)
                }
            }
        }
    }.distinctUntilChanged()
}

inline fun <T : Any, LIVE1 : Any, LIVE2 : Any> combine(
    initialValue: T,
    liveData1: LiveData<LIVE1>,
    liveData2: LiveData<LIVE2>,
    crossinline block: (T, LIVE1, LIVE2) -> T
): LiveData<T> {
    return MediatorLiveData<T>().apply {
        value = initialValue
        listOf(liveData1, liveData2).forEach { liveData ->
            addSource(liveData) {
                val currentValue = value
                val liveData1Value = liveData1.value
                val liveData2Value = liveData2.value
                if (currentValue != null && liveData1Value != null && liveData2Value != null) {
                    value = block(currentValue, liveData1Value, liveData2Value)
                }
            }
        }
    }.distinctUntilChanged()
}

inline fun <T : Any, LIVE1 : Any, LIVE2 : Any, LIVE3 : Any> combine(
    initialValue: T,
    liveData1: LiveData<LIVE1>,
    liveData2: LiveData<LIVE2>,
    liveData3: LiveData<LIVE3>,
    crossinline block: (T, LIVE1, LIVE2, LIVE3) -> T
): LiveData<T> {
    return MediatorLiveData<T>().apply {
        value = initialValue
        listOf(liveData1, liveData2, liveData3).forEach { liveData ->
            addSource(liveData) {
                val currentValue = value
                val liveData1Value = liveData1.value
                val liveData2Value = liveData2.value
                val liveData3Value = liveData3.value
                if (currentValue != null && liveData1Value != null &&
                    liveData2Value != null && liveData3Value != null
                ) {
                    value = block(currentValue, liveData1Value, liveData2Value, liveData3Value)
                }
            }
        }
    }.distinctUntilChanged()
}

inline fun <T : Any, LIVE1 : Any, LIVE2 : Any, LIVE3 : Any, LIVE4 : Any> combine(
    initialValue: T,
    liveData1: LiveData<LIVE1>,
    liveData2: LiveData<LIVE2>,
    liveData3: LiveData<LIVE3>,
    liveData4: LiveData<LIVE4>,
    crossinline block: (T, LIVE1, LIVE2, LIVE3, LIVE4) -> T
): LiveData<T> {
    return MediatorLiveData<T>().apply {
        value = initialValue
        listOf(liveData1, liveData2, liveData3, liveData4).forEach { liveData ->
            addSource(liveData) {
                val currentValue = value
                val liveData1Value = liveData1.value
                val liveData2Value = liveData2.value
                val liveData3Value = liveData3.value
                val liveData4Value = liveData4.value
                if (currentValue != null && liveData1Value != null &&
                    liveData2Value != null && liveData3Value != null &&
                    liveData4Value != null
                ) {
                    value = block(
                        currentValue,
                        liveData1Value,
                        liveData2Value,
                        liveData3Value,
                        liveData4Value
                    )
                }
            }
        }
    }.distinctUntilChanged()
}

inline fun <T : Any, LIVE1 : Any, LIVE2 : Any, LIVE3 : Any, LIVE4 : Any, LIVE5 : Any> combine(
    initialValue: T,
    liveData1: LiveData<LIVE1>,
    liveData2: LiveData<LIVE2>,
    liveData3: LiveData<LIVE3>,
    liveData4: LiveData<LIVE4>,
    liveData5: LiveData<LIVE5>,
    crossinline block: (T, LIVE1, LIVE2, LIVE3, LIVE4, LIVE5) -> T
): LiveData<T> {
    return MediatorLiveData<T>().apply {
        value = initialValue
        listOf(liveData1, liveData2, liveData3, liveData4, liveData5).forEach { liveData ->
            addSource(liveData) {
                val currentValue = value
                val liveData1Value = liveData1.value
                val liveData2Value = liveData2.value
                val liveData3Value = liveData3.value
                val liveData4Value = liveData4.value
                val liveData5Value = liveData5.value
                if (currentValue != null && liveData1Value != null &&
                    liveData2Value != null && liveData3Value != null &&
                    liveData4Value != null && liveData5Value != null
                ) {
                    value = block(
                        currentValue,
                        liveData1Value,
                        liveData2Value,
                        liveData3Value,
                        liveData4Value,
                        liveData5Value
                    )
                }
            }
        }
    }.distinctUntilChanged()
}

fun <T> LiveData<T>.setOnEach(mutableLiveData: MutableLiveData<T>): LiveData<T> {
    return map {
        mutableLiveData.value = it
        it
    }
}

fun <T : Any> LiveData<T?>.toNonNullSingleEvent(): LiveData<T> {
    val result = LiveEvent<T>()
    result.addSource(this) {
        if (it != null) {
            result.value = it
        }
    }
    return result
}

fun <T> merge(vararg liveDatas: LiveData<T>): LiveData<T> {
    return MediatorLiveData<T>().apply {
        liveDatas.forEach { liveData ->
            addSource(liveData) { value ->
                this.value = value
            }
        }
    }
}

fun <T : ErrorGettable> LiveData<T>.toAppError(): LiveData<AppError?> {
    return map {
        it.getErrorIfExists().toAppError()
    }
}

fun <T, R : Result<T>> LiveData<R>.fromResultToAppError(): LiveData<AppError?> {
    return map {
        it.exceptionOrNull()?.toAppError()
    }
}
