package org.dbs.analyst.repo.player

import org.dbs.analyst.model.player.PlayerStatus
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface PlayerStatusRepo : R2dbcRepository<PlayerStatus, Int> {
}
