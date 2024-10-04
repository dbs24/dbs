package org.dbs.mgmt.dao

import org.dbs.consts.Email
import org.dbs.consts.Login
import org.dbs.mgmt.repo.player.PlayerRepo
import org.dbs.player.PlayerCore.CacheKeyPlayerEnum.CC_PLAYER_ID
import org.dbs.player.PlayerCore.CacheKeyPlayerEnum.CC_PLAYER_LOGIN
import org.dbs.service.cache.EntityIdCacheService
import org.dbs.service.cache.v2.EntityCacheService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service
import org.dbs.mgmt.model.player.Player as ENTITY

@Service
class PlayerDao(
    val playerRepo: PlayerRepo,
    val entityIdCacheService: EntityIdCacheService,
    val entityCacheService: EntityCacheService<ENTITY>,
) : DaoAbstractApplicationService() {

    suspend fun findPlayerByLoginCo(login: Login): ENTITY? =
        entityCacheService.getEntity(CC_PLAYER_LOGIN, login) {
            playerRepo.findByLogin(login)
        }

    suspend fun findPlayerByEmailCo(email: Email): ENTITY? = playerRepo.findByEmail(email)

    fun invalidateCaches(playerLogin: Login)  {
            entityCacheService.invalidateCaches(playerLogin, CC_PLAYER_LOGIN)
            entityIdCacheService.invalidateCaches(playerLogin, CC_PLAYER_ID)
        }
}
