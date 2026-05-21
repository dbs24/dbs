package org.dbs.entity.core.v2.model

import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import java.io.Serializable

interface EntityCore : Serializable {

    val entityId: EntityId?
    val status: EntityStatusEnum
    val type: EntityTypeEnum
    val createDate: OperDate
    val modifyDate: OperDate
    val closeDate: OperDateNull

    fun entityInfo(): String = "(entity: $this)"
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogEntityAction(
    val action: String
)
