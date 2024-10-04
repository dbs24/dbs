package org.dbs.entity.core.v2.type

import org.dbs.entity.core.v2.consts.EntityKindId

interface EntityKind {
    val entityKindId: EntityKindId
    val entityKindName: String
    val entityType: EntityType
}
