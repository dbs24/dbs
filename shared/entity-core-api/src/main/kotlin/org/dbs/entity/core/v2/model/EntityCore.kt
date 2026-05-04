package org.dbs.entity.core.v2.model

import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import java.io.Serializable

interface EntityCore : Serializable {

    val entityId: EntityId?
    fun status(): EntityStatusEnum
    fun entityType(): EntityTypeEnum
    val createDate: OperDate
    val modifyDate: OperDate
    val closeDate: OperDateNull

    fun validateEntityCore() {
        require(status().entityType == entityType()) {
            "entityId: $entityId, status ${status()} belongs to ${status().entityType}, but entity type is ${entityType()}"
        }

        closeDate?.apply {
            require(this >= modifyDate) {
                "entityId: $entityId, modifyDate (${modifyDate}) is greater then closeDate (${closeDate})}"
            }
        }

        require(createDate <= modifyDate) {
            "entityId: $entityId, createDate (${createDate}) is greater then modifyDate (${modifyDate})}"
        }

    }
}
