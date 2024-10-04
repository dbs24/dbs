package org.dbs.service.repo

import org.dbs.service.api.NoSqlEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ReactiveCrudRepository<T : NoSqlEntity> : ReactiveCrudRepository<T, String> {

    //fun findSome(): Flux<T>
}

