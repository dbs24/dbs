package org.dbs.ext

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.NoArg2Mono
import org.dbs.consts.NoArg2Unit
import org.dbs.consts.SysConst.INTEGER_ZERO
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import kotlin.reflect.KCallable

object CollectionFuncs: Logging {
    inline fun <T, V: Any> Collection<T>.whenNoErrors(arg: NoArg2Mono<V>): Mono<V> =
        if (this.isEmpty()) arg().cache() else empty()

    inline fun <T> Collection<T>.whenEmpty(arg: NoArg2Unit) =
        if (this.isEmpty()) arg() else {
            // Empty body
        }

    fun Collection<Int>.faked(): Collection<Int> = run {
        this.takeIf { this.isNotEmpty() } ?: listOf(INTEGER_ZERO)
    }

    @Deprecated("use v2")
    fun <T : Any> Collection<T>.ensureNoDuplicates(vararg selectors: T.() -> KCallable<*>): Collection<T> {
        if (isEmpty()) return this

        for (selector in selectors) {
            // Получаем имя атрибута/метода из первого элемента
            val referenceName = this.first().selector().name

            val duplicates = this
                .groupBy { item ->
                    // метод .call() универсален: он вызывает get() для свойств или invoke() для функций
                    item.selector().call()
                }
                .filter { it.value.size > 1 }

            require(duplicates.isEmpty()) {
                val details = duplicates.entries.joinToString(separator = "\n") { (duplicateValue, items) ->
                    val classNames = items.map { it::class.simpleName }.distinct()
                    "Attribute '$referenceName' has duplicate value '$duplicateValue' found ${items.size} times in class '$classNames'"
                }
                "Duplicate validation failed:\n$details"
            }
        }
        return this
    }

    fun <T> Collection<T>.ensureNoDuplicates2(
        vararg selectors: (T) -> Any
    ): Collection<T> {

        if (this.isEmpty() || selectors.isEmpty()) return this

        selectors.forEach { selector ->
            val duplicates = this
                .groupBy(selector)
                .filter { (_, items) -> items.size > 1 }

            require(duplicates.isEmpty()) {
                duplicates.entries.joinToString("\n") { (value, items) ->
                    "Duplicate value '$value' found ${items.size} times in ${items.map { it!!::class.simpleName }}"
                }
            }
        }

        return this
    }

}
