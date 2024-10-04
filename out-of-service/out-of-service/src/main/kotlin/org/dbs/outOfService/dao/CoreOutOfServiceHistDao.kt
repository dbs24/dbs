package org.dbs.outOfService.dao

import org.dbs.outOfService.repo.CoreOutOfServiceHistRepo
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service

@Service
class CoreOutOfServiceHistDao internal constructor(
    val coreOutOfServiceHistRepo: CoreOutOfServiceHistRepo
) : DaoAbstractApplicationService() {
}
