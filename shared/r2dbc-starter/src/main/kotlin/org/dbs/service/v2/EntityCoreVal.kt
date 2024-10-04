package org.dbs.service.v2

import kotlinx.coroutines.reactor.awaitSingle
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.api.LateInitValNoInline
import org.dbs.consts.EntityId
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.model.Entity
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.service.v2.EntityCoreValExt.loadEntityCore
import org.dbs.spring.core.api.ServiceLocator.findService
import org.springframework.data.annotation.Transient
import reactor.core.publisher.Mono

abstract class EntityCoreVal(
    @Transient
    override val entityId: EntityId,
    @Transient
    val entityType: EntityTypeEnum,
) : EntityCore {

    @delegate:Transient
    @get:Transient
    val justCreated: LateInitValNoInline<Boolean> by lazy { LateInitValNoInline(false) }

    @delegate:Transient
    @get:Transient
    val newEntityStatus: LateInitValNoInline<EntityStatusEnum> by lazy { LateInitValNoInline("entityStatus") }

    @delegate:Transient
    @get:Transient
    val isClosed: LateInitValNoInline<Boolean> by lazy { LateInitValNoInline(false) }

    @delegate:Transient
    @get:Transient
    val entityCore: Entity by lazy { loadEntityCore() }

    @Transient
    override fun status() = entityCore.entityStatus

    companion object : Logging {

        @java.io.Serial
        private const val serialVersionUID: Long = 10L

        val r2dbService by lazy { findService(R2dbcPersistenceService::class) }

        suspend fun generateNewEntityId() = r2dbService.generateNewEntityIdV2().awaitSingle()

        fun <T : EntityCoreVal> executeAction(entity: T, ac: EntityActionEnum): Mono<T> = run {
            r2dbService.executeAction(entity, ac, EMPTY_STRING, EMPTY_STRING)
        }

        suspend fun <T : EntityCoreVal> executeAction(
            entity: T,
            ac: EntityActionEnum,
            remoteAddr: IpAddress,
            actionNote: StringNote,
        ): T = r2dbService.executeActionCo(entity, ac, remoteAddr, actionNote)
    }
}
