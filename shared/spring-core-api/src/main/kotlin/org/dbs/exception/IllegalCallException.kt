package org.dbs.exception

class IllegalCallException(private val exceptionMessage: String) : RuntimeException(exceptionMessage)
