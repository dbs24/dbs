package org.dbs.reactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.dbs.application.core.api.LateInitVal
import org.dbs.consts.SysConst.UNCHECKED_CAST
import reactor.core.publisher.Mono
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@JvmInline
value class MonoSyncSubscriber<T: Any>(private val mono: Mono<T>) {
    private fun complete(continuation: Continuation<Unit>) = continuation.resume(Unit)
    fun doSubscribe() {
        runBlocking(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                mono.doFinally { complete(continuation) }
                    .subscribe()
            }
        }
    }

    fun <T: Any> doBlockingSubscribe() = runBlocking(Dispatchers.IO) { doSubscribeThrowable<T>() }

    fun <T: Any> doBlockingNullAbleSubscribe() = runBlocking(Dispatchers.IO) { doSubscribeNullable<T>() }

    @Suppress(UNCHECKED_CAST)
    suspend fun <T: Any> doSubscribeThrowable(): T = run {
        val result = LateInitVal<T>("doSubscribeResult")
        val throwable = LateInitVal<Throwable>("doSubscribeThrowable")
        suspendCoroutine { continuation ->
            mono.doFinally { complete(continuation) }
                .subscribe({ t -> result.init(t as T) }, {
                    if (throwable.isNotInitialized()) {
                        throwable.init(it)
                    }
                })
        }
        if (throwable.isInitialized()) {
            error("${throwable.value}")
        }
        result.value
    }

    @Suppress(UNCHECKED_CAST)
    suspend fun <T: Any> doSubscribeNullable(): T? = run {
        val result = LateInitVal<T>("doSubscribeResult")
        val throwable = LateInitVal<Throwable>("doSubscribeThrowable")
        suspendCoroutine { continuation ->
            mono.doFinally { complete(continuation) }
                .subscribe(
                    { t -> result.init(t as T) },
                    {
                        if (throwable.isNotInitialized()) {
                            throwable.init(it)
                        }
                    }
                )
        }
        if (throwable.isInitialized()) {
            throw throwable.value
        }
        result.valueOrNull
    }

}
