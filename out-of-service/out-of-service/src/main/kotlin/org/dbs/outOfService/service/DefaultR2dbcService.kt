package org.dbs.outOfService.service

import org.dbs.api.PersistenceService
import org.dbs.consts.EntityId
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error

@Service
@EnableTransactionManagement
class DefaultR2dbcService(
    private val reactiveTransactionManager: ReactiveTransactionManager
) : AbstractApplicationService(), PersistenceService {

    override fun generateNewEntityId(): Mono<EntityId> =
        error(RuntimeException("Illegal operation - generateNewEntityId"))

    override val transactionalOperator: TransactionalOperator
        get() = TransactionalOperator.create(reactiveTransactionManager)

}
