package org.dbs.ext

import org.dbs.consts.NoArg2Mono
import org.dbs.consts.NoArg2Unit
import org.dbs.consts.SysConst.INTEGER_ZERO
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty

object CollectionFuncs {
    inline fun <T, V> Collection<T>.whenNoErrors(arg: NoArg2Mono<V>): Mono<V> =
        if (this.isEmpty()) arg().cache() else empty()

    inline fun <T> Collection<T>.whenEmpty(arg: NoArg2Unit) =
        if (this.isEmpty()) arg() else {
            // Empty body
        }

    fun Collection<Int>.faked(): Collection<Int> = run {
        this.takeIf { this.isNotEmpty() } ?: listOf(INTEGER_ZERO)
    }
}
