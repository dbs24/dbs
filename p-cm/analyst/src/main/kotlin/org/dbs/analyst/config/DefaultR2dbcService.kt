package org.dbs.analyst.config

import org.dbs.consts.SpringCoreConst.PropertiesNames.SELECT_NEXT_VAL_ACTION
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_NEXT_VAL_CMD
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.api.PersistenceService
import org.dbs.consts.EntityId
import org.springframework.beans.factory.annotation.Value
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Service
@EnableTransactionManagement
class DefaultR2dbcService(
    private val reactiveTransactionManager: ReactiveTransactionManager,
    private val databaseClient: DatabaseClient
) : AbstractApplicationService(), PersistenceService {

    @Value("\${$SPRING_R2DBC_NEXT_VAL_CMD:$SELECT_NEXT_VAL_ACTION}")
    private val nextValCmd = SELECT_NEXT_VAL_ACTION
    override fun <T> generateNewEntityId(entClass: KClass<T>): Mono<EntityId> =
        databaseClient.sql(nextValCmd)
            .map { row -> row.get(0, java.lang.Long::class.java) } //
            .one()
            .map { newId ->
                logger.debug("generate new entityId: $newId [${entClass.qualifiedName}]")
                newId.toLong()
            }

    override val transactionalOperator: TransactionalOperator
        get() = TransactionalOperator.create(reactiveTransactionManager)

}
