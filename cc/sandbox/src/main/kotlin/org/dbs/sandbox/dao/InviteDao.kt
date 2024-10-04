package org.dbs.sandbox.dao

import org.dbs.consts.EntityCode
import org.dbs.consts.Login
import org.dbs.invite.InviteCore.CacheKeyInviteEnum.CC_INVITE_CODE
import org.dbs.invite.InviteCore.CacheKeyInviteEnum.CC_INVITE_ID
import org.dbs.sandbox.repo.invite.InviteRepo
import org.dbs.service.cache.EntityIdCacheService
import org.dbs.service.cache.v2.EntityCacheService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service
import org.dbs.sandbox.model.invite.GameInvite as ENTITY

@Service
class InviteDao(
    val inviteRepo: InviteRepo,
    val entityIdCacheService: EntityIdCacheService,
    val entityCacheService: EntityCacheService<ENTITY>,
) : DaoAbstractApplicationService() {

    suspend fun findInviteByCode(code: EntityCode): ENTITY? =
        entityCacheService.getEntity(CC_INVITE_CODE, code) {
            inviteRepo.findByInviteCode(code)
        }

    fun invalidateCaches(playerLogin: Login)  {
        entityCacheService.invalidateCaches(playerLogin, CC_INVITE_CODE)
        entityIdCacheService.invalidateCaches(playerLogin, CC_INVITE_ID)
    }
}
