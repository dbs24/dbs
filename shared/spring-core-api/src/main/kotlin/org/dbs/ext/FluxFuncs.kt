package org.dbs.ext

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.Arg2Generic
import org.dbs.consts.NoArg2Flux
import org.dbs.exception.UnknownRefElementException
import org.dbs.reactor.MonoSyncSubscriber
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error
import reactor.core.publisher.Mono.justOrEmpty

object FluxFuncs : Logging {

    fun <T: Any> Int.noEmpty(func: NoArg2Flux<T>): Flux<T> = if (this > 0) func() else Flux.empty()
    fun <T: Any> Long.noEmpty(func: NoArg2Flux<T>): Flux<T> = if (this > 0) func() else Flux.empty()

    fun <T: Any> Flux<T>.validateDb(allDbRecords: Flux<T>, func: Arg2Generic<T, Boolean>): Flux<T> = allDbRecords
        .flatMap { if (!func(it)) raiseException(it) else justOrEmpty(it) }

    fun <T: Any> Flux<T>.validateDb(func: Arg2Generic<T, Boolean>): Flux<T> = validateDb(this, func)

    private fun <T: Any> raiseException(unknownDatabaseRecord: T): Mono<T> =
        justOrEmpty(unknownDatabaseRecord)
            .flatMap {
                error(
                    UnknownRefElementException(
                        "'${(it as Any).javaClass.canonicalName}'" +
                                ": Unknown record in database: '$unknownDatabaseRecord'"
                    )
                )
            }

    fun <T: Any> Mono<T>.subscribeMono(): T = MonoSyncSubscriber(this).doBlockingSubscribe()

    fun <T: Any> Mono<T>.subscribeEmptyMono(): T? = MonoSyncSubscriber(this).doBlockingNullAbleSubscribe()

    fun <T: Any, T1: Any> Mono<T>.mapDefaultIfEmpty(mapValue: T1) = map { mapValue }.defaultIfEmpty(mapValue)

    fun <T: Any, T1: Any> Mono<T>.mapSwitchIfEmpty(mapValue: Mono<T1>) = flatMap { mapValue }.switchIfEmpty(mapValue)
}
