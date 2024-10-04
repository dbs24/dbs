package org.dbs.application.core.service.funcs


fun interface CollectionProcessor<T> {
    fun processCollection(collection: MutableCollection<T>)
}