package org.dbs.outOfService.dao

import org.dbs.outOfService.model.CoreOutOfService
import org.dbs.outOfService.repo.CoreOutOfServiceRepo
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class CoreOutOfServiceDao internal constructor(
    val coreOutOfServiceRepo: CoreOutOfServiceRepo
) : DaoAbstractApplicationService() {

    fun findCoreOutOfService(): Flux<CoreOutOfService> = coreOutOfServiceRepo.findAll()
}
