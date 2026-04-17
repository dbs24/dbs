package org.dbs.entity.core.v2.status

import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.consts.EntityTypeId


typealias EntityStatusId = Int
typealias EntityStatusName = String

typealias AllowedStatusesRoutes = Map<EntityStatusEnum, Collection<EntityStatusEnum>>
