package org.dbs.entity.core.v2.type

import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.consts.EntityTypeId
import org.dbs.entity.core.v2.status.AllowedStatusesRoutes
import org.dbs.entity.core.v2.status.EntityStatusName
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityStatuses
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityTypes


object EntityTypeExtension: Logging {

    // Используем обычный MutableMap для O(1) поиска.
    // Если регистрация идет в рантайме из разных потоков — используйте ConcurrentHashMap.
    private val statusRoutesStore = mutableMapOf<EntityTypeEnum, AllowedStatusesRoutes>()

    fun EntityTypeEnum.registerAllowedStatusesChanges(routes: AllowedStatusesRoutes) =
        registerAllowedStatusesChangesInternal(routes)

    private fun EntityTypeEnum.registerAllowedStatusesChangesInternal(routes: AllowedStatusesRoutes): AllowedStatusesRoutes {
        val existing = findAllowedStatusesChanges()

        if (existing.isNotEmpty()) {
            logger.error { "Entity statuses routes already created ($this: (${existing.size} records) $existing)" }
            return existing
        }

        // Валидация: используем деструктуризацию (key, value) и стандартные коллекции
        routes.forEach { (sourceStatus, targetStatuses) ->
            require(sourceStatus.entityType == this) {
                "EntityStatuses mismatch (${sourceStatus.entityType} != $this)"
            }

            targetStatuses.forEach { target ->
                require(target.entityType == this) {
                    "Invalid entityStatus applied ($target), (applied entity ${target.entityType} != $this)"
                }
            }
        }

        logger.debug { "$this: register allowed statuses changes (${routes.size} records, $routes)" }

        statusRoutesStore[this] = routes
        return routes
    }

    fun EntityTypeEnum.findAllowedStatusesChanges(): AllowedStatusesRoutes =
        statusRoutesStore[this] ?: emptyMap()

    fun EntityTypeId.findAllowedStatusesChanges(): AllowedStatusesRoutes {
        val entityType = entityTypes.find { it.entityTypeId == this }
            ?: error("Unknown entity type ($this)")
        return entityType.findAllowedStatusesChanges()
    }

    fun EntityTypeEnum.findEntityStatus(statusName: EntityStatusName): EntityStatusEnum =
        entityStatuses.find { it.entityType == this && it.entityStatusName == statusName }
            ?: error("status not found ($statusName) for entityType $this")
}

