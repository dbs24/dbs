package org.dbs.mgmt.mapper

import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.mgmt.client.CreateOrUpdateLobbyRequest
import org.dbs.mgmt.model.lobby.Lobby
import org.dbs.mgmt.service.LobbyService
import org.dbs.service.v2.EntityCoreValExt.copyEntity

object LobbyMappers {

    fun LobbyService.updateLobby(src: Lobby, srcDto: CreateOrUpdateLobbyRequest): Lobby = src.copyEntity {
            src.copy(
                lobbyCode = srcDto.lobbyCode.grpcGetOrNull() ?: srcDto.oldLobbyCode,
            )
    }
}
