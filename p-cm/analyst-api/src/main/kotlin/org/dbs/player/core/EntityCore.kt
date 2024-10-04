package org.dbs.player.core

import org.dbs.entity.core.v2.type.Application.CORE
import org.dbs.entity.core.v2.type.EntityCoreInitializer
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.createEntityType

object EntityCore : EntityCoreInitializer {
    val PLAYER = createEntityType(10000u, "Player", CORE)
    val TEAM = createEntityType(10001u, "Team", CORE)
}
