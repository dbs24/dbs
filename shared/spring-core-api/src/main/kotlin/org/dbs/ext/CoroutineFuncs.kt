package org.dbs.ext

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

object CoroutineFuncs {

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun <T> Channel<T>.isReady(): Boolean = !(isClosedForReceive || isEmpty)
    fun Channel<*>.isReadyToReceive() = !isClosedForReceive && !isEmpty

    fun <T> Channel<T>.addItems(items: T) = runBlocking {
        send(items)
    }

    fun <T> Channel<T>.processItem(itemProcessor: suspend (T) -> Unit) = if (isReady()) {
        runBlocking {
            itemProcessor(receive())
        }
    } else {
    }

    fun createSuperVisorScope() = CoroutineScope(SupervisorJob())

}
