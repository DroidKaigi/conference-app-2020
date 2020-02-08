package io.github.droidkaigi.confsched2020.data.firestore.internal

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test

class FirestoreImplTest {
    @Ignore
    @Test
    fun thumbsUpIncrement() {
        val channel = BroadcastChannel<Unit>(10000)
        GlobalScope.launch {
            delay(100)
            println("send")
            channel.send(Unit)
            println("send")
            channel.send(Unit)
            println("send")
            channel.send(Unit)
            delay(600)
            println("send")
            channel.send(Unit)
            delay(200)
            println("send")
            channel.send(Unit)
            delay(200)
            println("send")
            channel.send(Unit)
            delay(200)
            println("send")
            channel.send(Unit)
            delay(600)
            channel.cancel()
        }
        runBlocking {
            var lastIndex = -1
            channel.asFlow()
                .withIndex()
                .debounce(300)
                .map {
                    val result = minOf(it.index - lastIndex, 50)
                    lastIndex = it.index
                    result
                }
                .collect {
                    println(it)
                }
        }
    }
}
