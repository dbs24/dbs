package org.dbs.service.v2

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.api.LateInitValNoInline
import org.dbs.consts.EntityId
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.spring.core.api.ServiceLocator.findService
import org.springframework.data.annotation.Transient

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
abstract class EntityCoreVal(
    @Transient
    override val entityId: EntityId,
    @Transient
    open val entityType: EntityTypeEnum,
) : EntityCore {

    @delegate:Transient
    @get:Transient
    @get:JsonIgnore
    val justCreated: LateInitValNoInline<Boolean> by lazy { LateInitValNoInline(false) }

    @delegate:Transient
    @get:Transient
    @get:JsonIgnore
    val newEntityStatus: LateInitValNoInline<EntityStatusEnum> by lazy { LateInitValNoInline("entityStatus") }

    @delegate:Transient
    @get:Transient
    @get:JsonIgnore
    val isClosed: LateInitValNoInline<Boolean> by lazy { LateInitValNoInline(false) }

    @Transient
    override fun status(): EntityStatusEnum =
        if (newEntityStatus.isInitialized()) newEntityStatus.value
        else error("status() not available for ${javaClass.simpleName}. Override status() or call asNew(status).")

    companion object : Logging {

        @java.io.Serial
        private const val serialVersionUID: Long = 10L

        @delegate:Transient
        @get:Transient
        val r2dbService by lazy { findService(R2dbcPersistenceService::class) }

        suspend fun generateNewEntityId() = r2dbService.generateNewEntityIdV2().awaitSingle()
    }
}
