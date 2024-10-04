package org.dbs.analyst.repo.solution

import org.dbs.analyst.model.solution.SolutionStatus
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface SolutionStatusRepo : R2dbcRepository<SolutionStatus, Int> {
}
