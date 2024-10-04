package org.dbs.application.core.service.funcs

import org.dbs.consts.Money
import org.dbs.consts.SysConst.UNCHECKED_CAST

object ServiceFuncs {
    @JvmField
    val COLLECTION_NULL: Collection<*>? = null

    @JvmField
    val MAP_NULL: Map<*, *>? = null
    val THROW_WHEN_NOT_FOUND = java.lang.Boolean.TRUE
    val SF_DONT_THROW_EXC = java.lang.Boolean.FALSE

    //==========================================================================
    fun <T> createCollection(): MutableCollection<T> = mutableListOf()

    //==========================================================================
    @JvmStatic
    fun <T> createCollection(cp: CollectionProcessor<T>): Collection<T> = createCollection<T>().also {
        cp.processCollection(it)
    }

    //==========================================================================
    @Suppress(UNCHECKED_CAST)
    fun <K, V> createMap(mapProcessor: MapProcessor<K, V>): HashMap<K, V> =
        HashMap<K, V>().also { mapProcessor.processMap(it) }

    //==========================================================================
    @Suppress(UNCHECKED_CAST)
    fun <T, V> createMap(): MutableMap<T, V> = HashMap()

    @Suppress(UNCHECKED_CAST)
    fun <T, V> createOrderedMap(): MutableMap<T, V> = LinkedHashMap()


    @Suppress(UNCHECKED_CAST)
    fun createS2SMap(): MutableMap<String, String> = HashMap()


    //==========================================================================
    @JvmStatic
    fun <K, V> createMap(k: K, v: V): Map<K, V> = createMap<K, V>().also { it[k] = v }
    infix fun <T> T.orUpdated(newValue: T): T = if (this == newValue) this else newValue
    infix fun Money.orUpdated(newValue: Money): Money =
        if (this.compareTo(newValue) == 0) this else newValue

    fun getSplitCommaParams(params: List<String>) = StringBuilder().also { sb ->
        params.forEach { sb.append(it).append(",") }
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1)
        }
    }.toString()
}
