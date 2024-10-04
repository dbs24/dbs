package org.dbs.api

import org.dbs.consts.EntityId
import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

interface PersistenceService : PublicApplicationBean {

    val transactionalOperator: TransactionalOperator

    fun generateNewEntityId(): Mono<EntityId>

}
