package io.github.droidkaigi.confsched2020.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.observe
import com.hadilq.liveevent.LiveEvent

inline fun <T : Any, LIVE1 : Any, LIVE2 : Any> composeBy(
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

inline fun <T : Any, LIVE1 : Any, LIVE2 : Any, LIVE3 : Any> composeBy(
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
                if (currentValue != null && liveData1Value != null && liveData2Value != null && liveData3Value != null) {
                    value = block(currentValue, liveData1Value, liveData2Value, liveData3Value)
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

interface LifecycleRunnable {
    fun observeBy(lifecycleOwner: LifecycleOwner)
}

fun <T> LiveData<T>.onChanged(
    observeBlock: (T) -> Unit
): LifecycleRunnable {
    return object : LifecycleRunnable {
        override fun observeBy(lifecycleOwner: LifecycleOwner) {
            observe(lifecycleOwner, observeBlock)
        }
    }
}
