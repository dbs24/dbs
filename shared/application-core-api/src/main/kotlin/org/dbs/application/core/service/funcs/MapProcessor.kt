package org.dbs.application.core.service.funcs

fun interface MapProcessor<K, V> {
    fun processMap(map: HashMap<K, V>)
}
