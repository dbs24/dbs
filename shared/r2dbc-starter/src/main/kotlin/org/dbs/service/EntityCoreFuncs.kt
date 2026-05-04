package org.dbs.service

import org.dbs.entity.core.v2.model.EntityCore

object EntityCoreFuncs {

    fun <T : EntityCore> T.validateEntityCore() {

            entityId?.apply {
                require(this > 0) {
                    "invalid entityId: ${entityInfo()}"
                }
            }

            require(status.entityType == type) {
                "status $status belongs to ${status.entityType}, but entity type is $type ${entityInfo()}"
            }

            closeDate?.apply {
                require(this.withNano(0) >= modifyDate.withNano(0)) {
                    "modifyDate (${modifyDate}) is greater then closeDate (${closeDate})} ${entityInfo()}"
                }
            }

            require(createDate <= modifyDate) {
                "createDate (${createDate}) is greater then modifyDate (${modifyDate}) ${entityInfo()}}"
            }
    }

}