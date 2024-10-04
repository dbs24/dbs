package org.dbs.analyst.repo.solution

import org.dbs.analyst.model.solution.Solution
import org.dbs.player.consts.Fen
import org.dbs.player.consts.SolutionId
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface SolutionRepo : R2dbcRepository<Solution, SolutionId> {
    fun findByFen(fen: Fen): Mono<Solution>
}
