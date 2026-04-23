package org.dbs.outOfService.service

import org.dbs.api.PersistenceService
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.reactive.TransactionalOperator

@Service
@EnableTransactionManagement
class DefaultR2dbcService(
    private val reactiveTransactionManager: ReactiveTransactionManager
) : AbstractApplicationService(), PersistenceService {

    override val transactionalOperator: TransactionalOperator
        get() = TransactionalOperator.create(reactiveTransactionManager)

}
