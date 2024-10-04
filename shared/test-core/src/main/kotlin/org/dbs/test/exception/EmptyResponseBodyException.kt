package org.dbs.test.exception

class EmptyResponseBodyException(routeName: String) : RuntimeException("$routeName: Response of body is empty")