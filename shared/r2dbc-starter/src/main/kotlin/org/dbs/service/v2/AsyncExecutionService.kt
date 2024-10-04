package org.dbs.service.v2

import kotlinx.coroutines.runBlocking
import org.dbs.consts.SpringCoreConst.PropertiesNames.ASYNC_ALG_STORE_DELAY
import org.dbs.consts.SpringCoreConst.PropertiesNames.ASYNC_ALG_STORE_DELAY_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.ASYNC_ALG_STORE_ENABLED
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
@Lazy(false)
@ConditionalOnProperty(name = [ASYNC_ALG_STORE_ENABLED], havingValue = STRING_TRUE)
class AsyncExecutionService(
    private val r2dbcPersistenceService: R2dbcPersistenceService
) : AbstractApplicationService() {

    @Value("\${$ASYNC_ALG_STORE_DELAY:$ASYNC_ALG_STORE_DELAY_VALUE}")
    private val asyncAlgDelay = ASYNC_ALG_STORE_DELAY_VALUE // Millis


    fun runLoop() = runBlocking {
        r2dbcPersistenceService.apply {
            entities4store.start(asyncAlgDelay) {
                with(it.asyncEntities) {
                    actExecOpt(
                        abstractEntities,
                        ac,
                        remoteAddr,
                        actionNote
                    ).subscribe()
                }
            }
            histEntities4store.start(asyncAlgDelay) {
                saveEntityHist(it.histEntity.entity).subscribe()
            }
        }
    }

    fun stopLoop() = runBlocking {
        logger.debug { "stopping infinite loop" }
        r2dbcPersistenceService.entities4store.stop()
        r2dbcPersistenceService.histEntities4store.stop()
    }

    override fun initialize() = super.initialize().also {
        logger.debug { "start infinite loop" }
        logger.debug { "asyncAlgDelay: $asyncAlgDelay ms" }
        runLoop()
    }

    override fun destroy() = super.destroy().also {
        stopLoop()
    }
}
