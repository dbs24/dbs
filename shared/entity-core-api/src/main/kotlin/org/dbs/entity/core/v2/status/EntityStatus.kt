package org.dbs.entity.core.v2.status

import org.dbs.entity.core.EntityStatusEnum


typealias EntityStatusId = Int
typealias EntityStatusName = String

typealias AllowedStatusesRoutes = Map<EntityStatusEnum, Collection<EntityStatusEnum>>
