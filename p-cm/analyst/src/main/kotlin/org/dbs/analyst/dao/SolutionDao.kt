package org.dbs.analyst.dao

import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.ext.FluxFuncs.validateDb
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.dbs.analyst.model.solution.Solution
import org.dbs.analyst.model.solution.SolutionStatus
import org.dbs.analyst.repo.solution.SolutionRepo
import org.dbs.analyst.repo.solution.SolutionStatusRepo
import org.dbs.player.enums.SolutionStatusEnum.Companion.isExistEnum
import org.dbs.player.enums.SolutionStatusEnum.entries
import org.dbs.service.api.RefSyncFuncs.synchronizeReference
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import kotlin.system.measureTimeMillis

@Service
class SolutionDao internal constructor(
    val solutionRepo: SolutionRepo,
    val solutionStatusRepo: SolutionStatusRepo,
) : DaoAbstractApplicationService() {

    val statuses by lazy { entries.map { SolutionStatus(it, it.getValue()) } }
    fun synchronizeRefs() = measureTimeMillis {
        fromIterable(statuses)
            .publishOn(parallelScheduler)
            .noDuplicates({ it }, { it.solutionStatusId }, { it.solutionStatusName })
            .synchronizeReference(solutionStatusRepo,
                { existItem, preparedItem -> existItem.id == preparedItem.id },
                { preparedItem -> preparedItem.copy() })

        solutionStatusRepo.findAll().validateDb { isExistEnum(it.id) }.count().subscribeMono()
    }.also { logger.debug { "synchronizeTransactionKinds: took $it ms" } }

    fun findSolutionByFen(login: String): Mono<Solution> = solutionRepo.findByFen(login)
    fun saveSolution(solution: Solution): Mono<Solution> = solutionRepo.save(solution)

}
