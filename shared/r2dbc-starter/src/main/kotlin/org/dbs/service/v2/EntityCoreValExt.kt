package org.dbs.service.v2

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.entity.core.EntityStatusEnum

object EntityCoreValExt : Logging {

    fun <T> EntityCoreVal.asNew(newStatus: EntityStatusEnum) = (this as T).also {
        justCreated.update(true)
        newEntityStatus.init(newStatus)
    }

    fun EntityCoreVal.updateStatus(newStatus: EntityStatusEnum, closed: Boolean) = apply {
        newEntityStatus.update(newStatus)
        if (closed) isClosed.update(true)
    }

    inline fun <T : EntityCoreVal> T.copyEntity(dtoApplier: (T) -> T): T =
        dtoApplier(this).let {
            if (newEntityStatus.isInitialized()) it.newEntityStatus.update(newEntityStatus.value)
            it.justCreated.update(this.justCreated.value)
            it
        }
}
