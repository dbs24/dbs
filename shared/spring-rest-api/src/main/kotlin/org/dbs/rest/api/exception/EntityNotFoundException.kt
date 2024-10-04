package org.dbs.rest.api.exception

class EntityNotFoundException(private val exceptionMessage: String) : RuntimeException(exceptionMessage)