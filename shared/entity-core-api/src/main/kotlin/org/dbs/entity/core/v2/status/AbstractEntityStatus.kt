package org.dbs.entity.core.v2.status

import org.dbs.entity.core.v2.consts.EntityTypeId

abstract class AbstractEntityStatus(
    override val entityStatusId: EntityStatusId,
    override val entityType: EntityTypeId,
    override val entityStatusName: String
) : EntityStatus {

    override fun toString() = let {"EntityStatus(entityStatusId=$entityStatusId;entityType=$entityType;entityStatusName=$entityStatusName)"}

}
