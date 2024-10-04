package org.dbs.test.exception

class EmptyCreatedEntityException(routeName: String) : RuntimeException("$routeName: Created entity is empty")