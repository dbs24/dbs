package org.dbs.outOfService.service

import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.consts.SysConst.OPERDATE_NULL
import org.dbs.consts.SysConst.OutOfServiceConsts.OUT_OF_SERVICE_FINISH_DATE
import org.dbs.consts.SysConst.OutOfServiceConsts.OUT_OF_SERVICE_NOTE
import org.dbs.consts.SysConst.OutOfServiceConsts.OUT_OF_SERVICE_START_DATE
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.outOfService.dao.CoreOutOfServiceDao
import org.dbs.outOfService.dao.CoreOutOfServiceHistDao
import org.dbs.outOfService.mapper.CoreOutOfServiceMappers.updateCoreOutOfService
import org.dbs.outOfService.model.CoreOutOfService
import org.dbs.outOfService.model.CoreOutOfServiceHist
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime.now

@Service
class CoreOutOfServiceService(
    val coreOutOfServiceDao: CoreOutOfServiceDao,
    val coreOutOfServiceHistDao: CoreOutOfServiceHistDao,
) : AbstractApplicationService() {

    override fun initialize() = super.initialize().also {
        // create default core out of service
        getOrCreateDefaultCoreOutOfService().subscribe()
    }

    fun getOrCreateDefaultCoreOutOfService(): Mono<CoreOutOfService> =
        findCoreOutOfService()
            .collectList()
            .flatMap { coreOutOfServices ->
                if (coreOutOfServices.isEmpty()) {
                    logger.info("Create default core out of service")
                    val coreOutOfService = updateCoreOutOfService(
                        createNewCoreOutOfService(),
                        OUT_OF_SERVICE_START_DATE,
                        OUT_OF_SERVICE_FINISH_DATE,
                        OUT_OF_SERVICE_NOTE
                    )
                    executeAction(coreOutOfService, true)
                } else {
                    coreOutOfServices.last().toMono()
                }
            }

    fun findCoreOutOfService(): Flux<CoreOutOfService> = coreOutOfServiceDao.findCoreOutOfService()

    fun createNewCoreOutOfService(): CoreOutOfService =
        CoreOutOfService(
            id = now().toLong(),
            createDate = now(),
            actualDate = OPERDATE_NULL,
            serviceDateStart = OPERDATE_NULL,
            serviceDateFinish = OPERDATE_NULL,
            note = STRING_NULL
        )

    fun executeAction(
        coreOutOfService: CoreOutOfService,
        justCreated: Boolean
    ): Mono<CoreOutOfService> =
        if (justCreated)
            coreOutOfServiceDao.coreOutOfServiceRepo.save(coreOutOfService.asNew())
        else coreOutOfServiceDao.coreOutOfServiceRepo.save(coreOutOfService)

    fun executeActionHist(
        coreOutOfService: CoreOutOfServiceHist,
        justCreated: Boolean
    ): Mono<CoreOutOfServiceHist> = run {
        if (!justCreated)
            coreOutOfServiceHistDao.coreOutOfServiceHistRepo.save(coreOutOfService.asNew())
        else coreOutOfService.toMono()
    }
}
