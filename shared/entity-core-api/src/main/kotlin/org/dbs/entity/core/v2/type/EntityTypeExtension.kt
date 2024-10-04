package org.dbs.entity.core.v2.type

import org.apache.logging.log4j.kotlin.logger
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.consts.EntityTypeId
import org.dbs.entity.core.v2.status.AllowedMapRoutes
import org.dbs.entity.core.v2.status.AllowedStatusesRoutes
import org.dbs.entity.core.v2.status.EntityStatusName
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityStatuses
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityTypes

object EntityTypeExtension {

    private val EntityTypeEnum.internalAllowedStatusesRoutes: AllowedMapRoutes by lazy { createMap() }
    private fun EntityTypeEnum.allowedStatusesChanges() = internalAllowedStatusesRoutes

    fun EntityTypeEnum.registerAllowedStatusesChanges(routes: AllowedStatusesRoutes) =
        registerAllowedStatusesChangesInternal(routes)

    private fun EntityTypeEnum.registerAllowedStatusesChangesInternal(routes: AllowedStatusesRoutes): AllowedStatusesRoutes =
        this.let { entityType ->
            entityType.findAllowedStatusesChanges()
                .apply {
                    if (this.values.isNotEmpty())
                        logger().error {
                            "Entity statuses routes already created " +
                                    "($entityType: (${this.values.size} records) ${this.values})"
                        }
                    else {
                        // validate entityTypes and entityStatuses
                        routes.asSequence().forEach {
                            //validate entityTypes
                            require(it.key.entityType == entityType) {
                                "EntityStatuses mismatch (${it.key.entityType} != ${entityType.entityTypeId}) "
                            }

                            // validate entityStatuses
                            it.value.asSequence().forEach { ti ->
                                require(ti.entityType == entityType)
                                {
                                    "Invalid entityStatus applied ($ti), " +
                                            "(applied entity ${ti.entityType} != ${entityType.entityTypeId}) "
                                }
                            }
                        }
                        logger.debug(
                            "$entityType: register allowed statuses changes " +
                                    "(${routes.size} records, (${routes})"
                        )
                        (allowedStatusesChanges() as MutableMap)[entityType] = routes
                    }
                }
        }

    fun EntityTypeEnum.findAllowedStatusesChanges(): AllowedStatusesRoutes = this.let { entityType ->
        allowedStatusesChanges()
                .entries
                .firstOrNull { it.key == entityType }?.value ?: createMap()
    }

    fun EntityTypeId.findAllowedStatusesChanges(): AllowedStatusesRoutes = run {
        (entityTypes.find { it.entityTypeId == this } ?: error(" Unknown entity type ($this) "))
            .findAllowedStatusesChanges()
    }

    fun EntityTypeEnum.findEntityStatus(statusName: EntityStatusName): EntityStatusEnum = run {
        entityStatuses.find { it.entityType == this && it.entityStatusName == statusName } ?: error("status not found ($statusName) for entityType $this")
    }

}
