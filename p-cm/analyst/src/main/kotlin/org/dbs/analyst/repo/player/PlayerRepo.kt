package org.dbs.analyst.repo.player

import org.dbs.consts.Email
import org.dbs.consts.EntityCode
import org.dbs.player.consts.PlayerId
import org.dbs.analyst.model.player.Player
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface PlayerRepo : R2dbcRepository<Player, PlayerId> {
    fun findByLogin(login: EntityCode): Mono<Player>
    fun findByEmail(email: Email): Mono<Player>
}
