package org.dbs.service.v2

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.OperDate
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.v2.model.Entity
import org.dbs.entity.core.v2.model.EntityState
import org.dbs.service.v2.EntityCoreVal.Companion.r2dbService
import java.time.LocalDateTime.now

object EntityCoreValExt : Logging {
    fun <T> EntityCoreVal.asNew(newStatus: EntityStatusEnum) = (this as T).also {
        justCreated.update(true)
        newEntityStatus.init(newStatus)
    }

    fun EntityCoreVal.updateStatus(newStatus: EntityStatusEnum, closed: Boolean) = apply {
        newEntityStatus.update(newStatus)
        if (closed) isClosed.update(true)
    }

    suspend fun <T : EntityCoreVal> T.reloadEntityCore(): EntityState = let {
        r2dbService.getEntityCore(it)
    }

    inline fun <T : EntityCoreVal> T.copyEntity(dtoApplier: (T) -> T): T =
        dtoApplier(this).let {
            it.newEntityStatus.update(entityCore.entityStatus)
            it.justCreated.update(this.justCreated.value)
            it
        }

    fun EntityCoreVal.loadEntityCore(): Entity = let {
        now().let {
            val entityCoreStatus: EntityStatusEnum
            val modifyDate: OperDate
            if (!justCreated.value) {
                runBlocking {
                    reloadEntityCore().apply {
                        entityCoreStatus = this.entityStatus
                        modifyDate = this.modifyDate
                    }
                }
            } else {
                entityCoreStatus = newEntityStatus.value
                modifyDate = it
            }

            Entity(
                entityId,
                entityType,
                entityCoreStatus,
                it,
                modifyDate,
                it.takeIf { isClosed.value })
        }
    }
}
