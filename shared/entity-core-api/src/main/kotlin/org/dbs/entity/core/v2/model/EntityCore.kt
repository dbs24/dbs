package org.dbs.entity.core.v2.model

import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.springframework.data.domain.Persistable
import java.io.Serializable

interface EntityCore : Persistable<Long>, Serializable {

    val entityId: Long?
    val status: EntityStatusEnum
    val type: EntityTypeEnum
    val createDate: OperDate
    val modifyDate: OperDate
    val closeDate: OperDateNull

    fun entityInfo(): String = "(entity: $this)"

    override fun getId() = entityId
    override fun isNew() = (entityId == null)
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogEntityAction(
    val action: String
)
