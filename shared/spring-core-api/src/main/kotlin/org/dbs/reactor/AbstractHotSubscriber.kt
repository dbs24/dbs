package org.dbs.reactor

import reactor.core.publisher.SignalType
import reactor.core.publisher.Sinks
import java.util.concurrent.locks.LockSupport


abstract class AbstractHotSubscriber<T> : AbstractSubscriber<T>() {
    val hotSource = Sinks.many().multicast().directBestEffort<T>()
    val hotFlux = hotSource.asFlux()
    override fun initialize() {
        super.initialize()
        hotFlux.subscribe(this)
    }

    @Throws(Exception::class)
    override fun destroy() {
        hotSource.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST)
        super.destroy()
    }

    override fun onNext(t: T) {
        processEvent(t)
    }

//    fun emitEvent(t: T) {
//        hotSource.emitNext(t, retryOnNonSerializedElse(Sinks.EmitFailureHandler.FAIL_FAST))
//    }

    protected abstract fun processEvent(t: T)

    companion object {
        fun retryOnNonSerializedElse(fallback: Sinks.EmitFailureHandler): Sinks.EmitFailureHandler {

            return Sinks.EmitFailureHandler { signalType: SignalType, emitResult: Sinks.EmitResult ->

                if (emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED) {
                    LockSupport.parkNanos(10)
                    true
                } else {
                    fallback.onEmitFailure(signalType, emitResult)
                }
            }
        }
    }
}
