package org.dbs.service.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.ReflectionFuncs.createPkgClassesCollection
import org.dbs.consts.EntityStatusId
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.dbs.entity.core.ActionCode
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityStatus
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityType
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.ext.CollectionFuncs.ensureNoDuplicates2
import org.dbs.service.repo.ActionCodeRepository
import org.dbs.service.repo.EntityStatusRepository
import org.dbs.service.repo.EntityTypeRepository
import org.dbs.spring.core.api.AbstractApplicationBean
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.context.annotation.Lazy
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component

@Component
@Lazy(false)
class CoreEnumsSynchronizer(
    val entityTypeRepository: EntityTypeRepository,
    val entityStatusRepository: EntityStatusRepository,
    val actionCodeRepository: ActionCodeRepository,
) : AbstractApplicationBean(), SmartInitializingSingleton, Logging {

    data class EnumMetadata(
        val types: List<EntityTypeEnum>,
        val statuses: List<EntityStatusEnum>,
        val actions: List<EntityActionEnum>
    )

    val metadata: EnumMetadata =
        EnumMetadata(
            types = getEnums(EntityTypeEnum::class.java),
            statuses = getEnums(EntityStatusEnum::class.java),
            actions = getEnums(EntityActionEnum::class.java)
        )

    private fun <T> getEnums(clazz: Class<T>): List<T> {
        return createPkgClassesCollection(ALL_PACKAGES, clazz)
            .filter { it.isEnum }
            .flatMap { it.enumConstants.toList() }
            .map { it as T }
            .also {
                require(it.isNotEmpty()) { "enum list of ${clazz.simpleName} is empty" }
            }
    }

    override fun afterSingletonsInstantiated() {
        // Запускаем корутину в фоне, так как метод жизненного цикла Spring синхронный
        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncAll()
            } catch (e: Exception) {
                logger.error("Failed to synchronize enums", e)
            }
        }
    }

    // Главный метод синхронизации теперь последовательный и понятный
    private suspend fun syncAll() {
        sync(
            enumValues = metadata.types,
            repo = entityTypeRepository,
            mapToEntity = { e ->
                EntityType(
                    entityTypeId = e.entityTypeId,
                    entityTypeName = e.entityTypeName,
                    application = e.module.name
                )
            },
            uniqueBy = arrayOf(
                { it.entityTypeId },
                { it.entityTypeName }
            ),
            copyNew = { t ->
                EntityType(
                    entityTypeId = t.entityTypeId,
                    entityTypeName = t.entityTypeName,
                    application = t.application
                )
            }
        )

        coroutineScope {
            val statusesJob = async {
                sync(
                    enumValues = metadata.statuses,
                    repo = entityStatusRepository,
                    mapToEntity = { e ->
                        EntityStatus(
                            entityStatus = e.entityStatusId,
                            entityType = e.entityType.entityTypeId,
                            entityStatusName = e.entityStatusName
                        )
                    },
                    uniqueBy = arrayOf(
                        { it.entityStatus },
                        { it.entityStatusName }
                    ),
                    copyNew = { t ->
                        EntityStatus(
                            entityStatus = t.entityStatus,
                            entityType = t.entityType,
                            entityStatusName = t.entityStatusName
                        )
                    }
                )
            }

            val actionsJob = async {
                sync(
                    enumValues = metadata.actions,
                    repo = actionCodeRepository,
                    mapToEntity = { e ->
                        ActionCode(
                            actionCode = e.actionCodeId,
                            actionName = e.actionName,
                            appName = e.actionName,
                            isClosed = false
                        )
                    },
                    uniqueBy = arrayOf(
                        { it.actionCode },
                        { it.actionName },
                        { it.appName }
                    ),
                    copyNew = { t ->
                        ActionCode(
                            actionCode = t.actionCode,
                            actionName = t.actionName,
                            appName = t.appName,
                            isClosed = t.isClosed
                        )
                    }
                )
            }

            statusesJob.await()
            actionsJob.await()
        }
    }

    suspend fun <E, Id : Any, T : AbstractRefEntity<Id>> sync(
        enumValues: Collection<E>,
        repo: CoroutineCrudRepository<T, Id>,
        mapToEntity: (E) -> T,
        uniqueBy: Array<(T) -> Any>,
        copyNew: (T) -> T
    ) {
        if (enumValues.isEmpty()) {
            logger.warn("Enum list is empty, nothing to sync")
            return
        }

        val enumClass = enumValues.first()?.javaClass?.name ?: error("Not found")
        val prepared = enumValues.map(mapToEntity)

        prepared.ensureNoDuplicates2(*uniqueBy)

        val existingMap = repo.findAll().toList().associateBy { it.id }

        val toSave = prepared.mapNotNull { preparedItem ->
            val existing = existingMap[preparedItem.id]

            when {
                existing == null -> copyNew(preparedItem).asNew()
                existing != preparedItem -> preparedItem
                else -> null
            }
        }

        if (toSave.isEmpty()) {
            logger.info("No changes for $enumClass")
        } else {
            logger.info("Saving ${toSave.size} items for $enumClass")
            repo.saveAll(toSave).toList() // Вызов .toList() или .collect() триггерит холодный Flow
        }
    }

    fun findEntityStatus(entityStatusId: EntityStatusId): EntityStatusEnum = let {
        metadata.statuses.find { it.entityStatusId == entityStatusId }
            ?: error("Unknown entity statusId (${entityStatusId})")
    }
}
