package org.dbs.entity.core.v2.model

import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum

data class EntityState(
    val entityStatus: EntityStatusEnum,
    val modifyDate: OperDate,
    val closeDate: OperDateNull = null
)
