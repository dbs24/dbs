package org.dbs.player.dto.player

import org.dbs.consts.EntityStatusName
import org.dbs.consts.Password
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.player.PlayerLogin
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.ResponseDto


data class PlayerAuthDto(
    val playerLogin: PlayerLogin,
    val playerPassword: Password,
    val playerStatus: EntityStatusName
) : ResponseDto

data class GetPlayerCredentialsResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<PlayerAuthDto>(httpRequestId)
