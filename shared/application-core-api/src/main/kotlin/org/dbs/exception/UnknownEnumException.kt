package org.dbs.exception

class UnknownEnumException(value: String) : RuntimeException("$value: Response of body is empty")
