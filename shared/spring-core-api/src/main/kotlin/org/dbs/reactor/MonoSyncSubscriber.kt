package org.dbs.reactor

import kotlinx.coroutines.runBlocking
import org.dbs.application.core.api.LateInitVal
import org.dbs.consts.SysConst.UNCHECKED_CAST
import reactor.core.publisher.Mono
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@JvmInline
value class MonoSyncSubscriber<T>(private val mono: Mono<T>) {
    private fun complete(continuation: Continuation<Unit>) = continuation.resume(Unit)
    fun doSubscribe() {
        runBlocking {
            suspendCoroutine { continuation ->
                mono.doFinally { complete(continuation) }
                    .subscribe()
            }
        }
    }

    fun <T> doBlockingSubscribe() = runBlocking { doSubscribeThrowable<T>() }

    fun <T> doBlockingNullAbleSubscribe() = runBlocking { doSubscribeNullable<T>() }

    @Suppress(UNCHECKED_CAST)
    suspend fun <T> doSubscribeThrowable(): T = run {
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
    suspend fun <T> doSubscribeNullable(): T? = run {
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
