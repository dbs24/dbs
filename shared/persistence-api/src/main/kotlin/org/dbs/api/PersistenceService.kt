package org.dbs.api

import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.transaction.reactive.TransactionalOperator

interface PersistenceService : PublicApplicationBean {

    val transactionalOperator: TransactionalOperator

}
