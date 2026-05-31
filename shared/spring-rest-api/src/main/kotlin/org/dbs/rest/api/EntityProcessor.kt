package org.dbs.rest.api

@Deprecated("to remove")
fun interface EntityProcessor<T, V> {
    fun processEntity(entity: T?): V
}