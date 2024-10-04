package org.dbs.analyst.service.grpc

import org.dbs.spring.core.api.PublicApplicationBean
import io.grpc.Context
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import net.devh.boot.grpc.server.service.GrpcService
import org.dbs.cm.client.*
import org.dbs.analyst.service.PlayerService
import org.dbs.analyst.service.grpc.GrpcCreateOrUpdatePlayer.createOrUpdatePlayerInternal
import org.dbs.analyst.service.grpc.GrpcGetPlayerCredentials.getPlayerCredentialsInternal
import org.dbs.analyst.service.grpc.GrpcUpdatePlayerStatus.updatePlayerStatusInternal
import org.dbs.kafka.service.KafkaUniversalService
import org.dbs.player.PlayerLogin
import org.dbs.player.consts.PlayersConsts.Claims.CL_PLAYER_LOGIN
import org.dbs.service.AbstractGrpcServerService
import org.springframework.stereotype.Service

@Service
class CmGrpcService(
    val playerService: PlayerService,
    val kafkaUniversalService: KafkaUniversalService,
) : AbstractGrpcServerService(), PublicApplicationBean {

    override fun <ReqT, RespT> interceptCallInternal(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>,
    ): Context = call.run {

        Context.current()
            .addPlayerLogin(call, headers)
            .also { logger.debug { "currentContext: $it" } }
    }

    // overridable - default jwt validator
//    override val jwtValidator: JwtValidator = { service, jwt ->
//        GRPC_IS_AUTHORIZED.takeIf { service.validateJwt(jwt) } ?: JWT_AUTHORIZATION_FAIL
//    }

//    private val proceduresRequiresPlayerLogin by lazy { setOf(CK_PLAYER_UPDATE_PASSWORD_PROCEDURE) }

    fun <ReqT, RespT> Context.addPlayerLogin(call: ServerCall<ReqT, RespT>, headers: Metadata): Context =
//        if (proceduresRequiresPlayerLogin.isContainProcedure(call.getProcedureName())) {
//            this.withValue(CK_PLAYER_LOGIN, headers.getPlayerLogin())
//        } else
        this

    fun Metadata.getPlayerLogin(): PlayerLogin = getJwtClaim(CL_PLAYER_LOGIN)

    @GrpcService
    inner class AnalystService : AnalystClientServiceGrpcKt.AnalystClientServiceCoroutineImplBase(),
        PublicApplicationBean {
        override suspend fun getPlayerCredentials(request: PlayerCredentialsRequest) =
            getPlayerCredentialsInternal(request)

        override suspend fun createOrUpdatePlayer(request: CreateOrUpdatePlayerRequest) =
            createOrUpdatePlayerInternal(request)

        override suspend fun updatePlayerStatus(request: UpdatePlayerStatusRequest) =
            updatePlayerStatusInternal(request)

        override suspend fun getFenSolution(request: GetSolutionRequest): CreatedSolution =
             getFenSolution(request)
    }
}
