package org.dbs.analyst.service

import org.dbs.analyst.dao.SolutionDao
import org.dbs.analyst.mapper.SolutionMappers
import org.dbs.analyst.model.solution.Solution
import org.dbs.consts.EntityStatusName
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.entity.core.enums.EntityStatusEnum
import org.dbs.entity.core.enums.EntityTypeEnum.SSS_ACTORS_MANAGER
import org.dbs.player.consts.Fen
import org.dbs.player.enums.SolutionStatusEnum.SS_ACTUAL
import org.dbs.rest.service.value.AbstractRestApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
@Lazy(false)
class SolutionService(
    val solutionDao: SolutionDao,
    val solutionMappers: SolutionMappers,
) : AbstractRestApplicationService() {

    override fun initialize() = super.initialize().also {
        solutionDao.synchronizeRefs()
    }

    fun findSolutionByFen(fen: Fen): Mono<Solution> =
        solutionDao.findSolutionByFen(fen
            .also { logger.debug { "find solution: $fen" } })

    fun createNewSolution(fen: Fen): Mono<Solution> =
        generateNewEntityId(Solution::class.java)
            .map { newEntityId ->
                logger.debug { "create new solution: $fen (entityId=$newEntityId)" }
                Solution(
                    solutionId = newEntityId,
                    playerId = 0L,
                    status = SS_ACTUAL,
                    fen = EMPTY_STRING,
                    depth = 0,
                    move = EMPTY_STRING,
                    timeout = 0,
                    whiteMove = true
                )
            }

    fun setSolutionNewStatus(solution: Solution, status: EntityStatusName): Mono<Solution> =
        solution.run {
            val entityStatusEnum = EntityStatusEnum.getEnum(SSS_ACTORS_MANAGER, status)
//            updateStatus(
//                entityStatusEnum, (entityStatusEnum == SSS_ACTORS_MANAGER_STATUS_CLOSED)
//                        || (entityStatusEnum == SSS_ACTORS_MANAGER_STATUS_BANNED)
//            )
            toMono()
        }

    fun saveSolution(solution: Solution): Mono<Solution> = solutionDao.saveSolution(solution)
}
