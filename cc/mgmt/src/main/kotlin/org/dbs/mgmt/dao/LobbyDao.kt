package org.dbs.mgmt.dao

import org.dbs.consts.Login
import org.dbs.lobby.LobbyCore.CacheKeyActionEnum.CC_LOBBY_CODE
import org.dbs.lobby.LobbyCore.CacheKeyActionEnum.CC_LOBBY_ID
import org.dbs.mgmt.repo.lobby.LobbyRepo
import org.dbs.service.cache.EntityIdCacheService
import org.dbs.service.cache.v2.EntityCacheService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service
import org.dbs.mgmt.model.lobby.Lobby as ENTITY

@Service
class LobbyDao(
    val lobbyRepo: LobbyRepo,
    val entityIdCacheService: EntityIdCacheService,
    val entityCacheService: EntityCacheService<ENTITY>,
) : DaoAbstractApplicationService() {

    suspend fun findLobbyByCode(lobbyCode: Login): ENTITY? =
        entityCacheService.getEntity(CC_LOBBY_CODE, lobbyCode) {
            lobbyRepo.findByLobbyCode(lobbyCode)
        }

    fun invalidateCaches(lobbyLogin: Login) {
        entityCacheService.invalidateCaches(lobbyLogin, CC_LOBBY_CODE)
        entityIdCacheService.invalidateCaches(lobbyLogin, CC_LOBBY_ID)
    }
}
