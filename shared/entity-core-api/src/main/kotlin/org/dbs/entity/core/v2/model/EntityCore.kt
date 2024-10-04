package org.dbs.entity.core.v2.model

import org.dbs.consts.EntityId
import org.dbs.entity.core.EntityStatusEnum
import java.io.Serializable

interface EntityCore : Serializable {

    val entityId: EntityId
    fun status(): EntityStatusEnum
}
