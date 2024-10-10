package org.dbs.auth.server.service.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.dbs.auth.server.clients.industrial.grpc.GrpcUserLogin.loginUserInternal
import org.dbs.auth.server.clients.industrial.grpc.GrpcUserRefreshJwt.userRefreshJwtInternal
import org.dbs.auth.server.clients.players.grpc.GrpcFindJwt.findJwt
import org.dbs.auth.server.clients.players.grpc.GrpcPlayerLogin.loginPlayerInternal
import org.dbs.auth.server.clients.players.grpc.GrpcPlayerRefreshJwt.playerRefreshJwtInternal
import org.dbs.auth.server.consts.GrpcConsts.CK_FIND_JWT_PROCEDURE
import org.dbs.auth.server.consts.GrpcConsts.CK_PLAYER_LOGIN_PROCEDURE
import org.dbs.auth.server.consts.GrpcConsts.CK_USER_LOGIN_PROCEDURE
import org.dbs.auth.server.service.AuthServiceLayer.Companion.authServerGrpcService
import org.dbs.protobuf.auth.*
import org.dbs.service.AbstractGrpcServerService
import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServerGrpcService(
    val passwordEncoder: PasswordEncoder,
) : AbstractGrpcServerService(), PublicApplicationBean {

    // white grpc procedures
    override val whiteProcs = setOf(CK_PLAYER_LOGIN_PROCEDURE, CK_FIND_JWT_PROCEDURE, CK_USER_LOGIN_PROCEDURE)

    @GrpcService
    inner class AuthServerService : AuthServerClientServiceGrpcKt.AuthServerClientServiceCoroutineImplBase(),
        PublicApplicationBean {
        // jwt
        override suspend fun findJwt (request: FindJwtRequest) =
            authServerGrpcService.findJwt(request)

        // players
        override suspend fun playerLogin(request: PlayerLoginRequest) =
            authServerGrpcService.loginPlayerInternal(request)

        override suspend fun refreshPlayerJwt(request: RefreshPlayerJwtRequest) =
            authServerGrpcService.playerRefreshJwtInternal(request)

        // users
        override suspend fun userLogin(request: UserLoginRequest) =
            authServerGrpcService.loginUserInternal(request)

        override suspend fun refreshUserJwt(request: RefreshUserJwtRequest) =
            authServerGrpcService.userRefreshJwtInternal(request)

    }
}
