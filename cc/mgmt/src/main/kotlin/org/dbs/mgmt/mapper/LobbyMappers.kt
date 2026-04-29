package org.dbs.mgmt.mapper

import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.mgmt.client.CreateOrUpdateLobbyRequest
import org.dbs.mgmt.model.lobby.Lobby
import org.dbs.mgmt.service.LobbyService
import java.time.LocalDateTime.now

object LobbyMappers {

    fun LobbyService.updateLobby(src: Lobby, srcDto: CreateOrUpdateLobbyRequest): Lobby =
        src.copy(
            lobbyCode = srcDto.lobbyCode.grpcGetOrNull() ?: srcDto.oldLobbyCode,
            lobbyName = srcDto.lobbyName.grpcGetOrNull() ?: src.lobbyName,
            lobbyKind = srcDto.lobbyKind.grpcGetOrNull()?.toIntOrNull() ?: src.lobbyKind,
            modifyDate = now(),
        )
}
