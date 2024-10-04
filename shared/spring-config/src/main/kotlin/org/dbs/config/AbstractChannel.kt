package org.dbs.config

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.api.LateInitVal
import org.dbs.consts.SuspendGenericArg2Unit
import org.dbs.ext.CoroutineFuncs.isReadyToReceive


abstract class AbstractChannel<T> : Logging {

    private val channel by lazy { Channel<T>(UNLIMITED) }
    private val job by lazy { LateInitVal<Job>() }
    private val thread by lazy { LateInitVal<Thread>() }

    fun push(element: T) = runBlocking {  channel.send(element.also {
        logger.debug{ "push: $element"}
    }) }

    suspend fun start(asyncAlgDelay: Long, funcExec: SuspendGenericArg2Unit<T>) {
        thread.init(Thread {
            runBlocking {
                job.init(launch {
                    while (true) {
                        if (channel.isReadyToReceive()) {
                            channel.receive().let {
                                logger.debug { "receive entities = [$it]" }
                                funcExec(it)
                            }
                        }
                        delay(asyncAlgDelay)
                    }
                })
            }
        }.also {
            logger.debug { "create coroutine channel (${this.javaClass.canonicalName})" }
            it.start() })
    }

    fun stop() = runBlocking {
        logger.debug { "stopping infinite loop" }
        job.value.cancel()
        thread.value.apply {
            if (isAlive) interrupt()
        }
    }
}
