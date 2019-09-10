package io.github.droidkaigi.confsched2020.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

inline fun <T : Any, LIVE1 : Any, LIVE2 : Any> composeBy(
    initialValue: T,
    liveData1: LiveData<LIVE1>,
    liveData2: LiveData<LIVE2>,
    crossinline block: (T, LIVE1, LIVE2) -> T
): LiveData<T> {
    return MediatorLiveData<T>().apply {
        value = initialValue
        addSource(liveData1) { data ->
            callBlockWhenNonNullValue(liveData1, liveData2, block)
        }
        addSource(liveData2) { data ->
            callBlockWhenNonNullValue(liveData1, liveData2, block)
        }
    }
}

inline fun <LIVE1 : Any, LIVE2 : Any, T : Any> MediatorLiveData<T>.callBlockWhenNonNullValue(
    liveData1: LiveData<LIVE1>,
    liveData2: LiveData<LIVE2>,
    crossinline block: (T, LIVE1, LIVE2) -> T
) {
    val currentValue = value
    val liveData1Value = liveData1.value
    val liveData2Value = liveData2.value
    if (currentValue != null && liveData1Value != null && liveData2Value != null) {
        value = block(currentValue, liveData1Value, liveData2Value)
    }
}
