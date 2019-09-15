package io.github.droidkaigi.confsched2020.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.hadilq.liveevent.LiveEvent

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