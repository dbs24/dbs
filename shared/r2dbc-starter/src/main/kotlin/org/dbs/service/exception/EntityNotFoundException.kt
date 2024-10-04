package org.dbs.service.exception

class EntityNotFoundException(private val exceptionMessage: String) : RuntimeException(exceptionMessage)