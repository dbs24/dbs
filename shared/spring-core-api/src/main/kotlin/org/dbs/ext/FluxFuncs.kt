package org.dbs.ext

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.Arg2Generic
import org.dbs.consts.NoArg2Flux
import org.dbs.exception.DuplicatedEnumException
import org.dbs.exception.UnknownRefElementException
import org.dbs.reactor.MonoSyncSubscriber
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error
import reactor.core.publisher.Mono.justOrEmpty
import reactor.kotlin.core.publisher.switchIfEmpty

object FluxFuncs : Logging {

    fun <T> Int.noEmpty(func: NoArg2Flux<T>): Flux<T> = if (this > 0) func() else Flux.empty()
    fun <T> Long.noEmpty(func: NoArg2Flux<T>): Flux<T> = if (this > 0) func() else Flux.empty()

    private fun <T> Flux<T>.noDuplicatesInternal(groupExpr: Arg2Generic<T, Any>) = this.groupBy(groupExpr)
        .flatMap { it.collectList().map { counts -> Pair(it.key(), counts.size) } }
        .filter { it.second > 1 }
        .flatMap<T> { raiseDuplicateException(it, this) }
        .switchIfEmpty(this)

    fun <T> Flux<T>.noDuplicates(vararg fieldsList: Arg2Generic<T, Any>): Flux<T> = this.run {
        fromIterable(fieldsList.asList())
            .flatMap { this.noDuplicatesInternal(it) }
            .filter { false }
            .switchIfEmpty(this)
    }

    private fun <T> raiseDuplicateException(pair: Pair<Any, Int>, flux: Flux<T>): Mono<T> =
        flux.take(1)
            .single()
            .flatMap {
                error(
                    DuplicatedEnumException(
                        "Enum/reference '${(it as Any).javaClass.canonicalName ?: it }'" +
                                " (key = '${pair.first}') contains ${pair.second} duplicated entries "
                    )
                )
            }

    fun <T> Flux<T>.validateDb(allDbRecords: Flux<T>, func: Arg2Generic<T, Boolean>): Flux<T> = allDbRecords
        .flatMap { if (!func(it)) raiseException(it) else justOrEmpty(it) }

    fun <T> Flux<T>.validateDb(func: Arg2Generic<T, Boolean>): Flux<T> = validateDb(this, func)

    private fun <T> raiseException(unknownDatabaseRecord: T): Mono<T> =
        justOrEmpty(unknownDatabaseRecord)
            .flatMap {
                error(
                    UnknownRefElementException(
                        "'${(it as Any).javaClass.canonicalName}'" +
                                ": Unknown record in database: '$unknownDatabaseRecord'"
                    )
                )
            }

//    fun <T, R> Mono<T>.flatMapSuspend(func: suspend (T) -> Mono<R>): Mono<R> =
//        flatMap { runBlocking { func(it) } }
//
//    fun <T, R> Mono<T>.mapSuspend(func: suspend (T) -> R): Mono<R> =
//        map { runBlocking { func(it) } }

    fun <T> Mono<T>.subscribeMono(): T = MonoSyncSubscriber(this).doBlockingSubscribe()

    fun <T> Mono<T>.subscribeEmptyMono(): T? = MonoSyncSubscriber(this).doBlockingNullAbleSubscribe()

    fun <T, T1> Mono<T>.mapDefaultIfEmpty(mapValue: T1) = map { mapValue }.defaultIfEmpty(mapValue)

    fun <T, T1> Mono<T>.mapSwitchIfEmpty(mapValue: Mono<T1>) = flatMap { mapValue }.switchIfEmpty { mapValue }
}
