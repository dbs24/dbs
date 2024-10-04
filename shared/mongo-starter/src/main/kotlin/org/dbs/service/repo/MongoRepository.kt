package org.dbs.service.repo

import org.dbs.service.api.NoSqlEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface MongoRepository<T : NoSqlEntity> : ReactiveMongoRepository<T, String> {

    //fun findSome(): Flux<T>
}