package org.dbs.rest.api

fun interface EntityProcessor<T, V> {
    fun processEntity(entity: T?): V
}