package org.dbs.auth.verify.service.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.dbs.auth.verify.clients.auth.api.JwtAttrs
import org.dbs.auth.verify.clients.auth.grpc.GrpcJwtVerify.verifyJwtInternal
import org.dbs.auth.verify.consts.AuthVerifyConsts.CK_VERIFY_JWT_PROCEDURE
import org.dbs.auth.verify.service.AuthServiceLayer.Companion.jwtVerifyGrpcService
import org.dbs.protobuf.auth.AuthVerifyClientServiceGrpcKt
import org.dbs.protobuf.auth.VerifyJwtRequest
import org.dbs.service.AbstractGrpcServerService
import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class JwtVerifyGrpcService : AbstractGrpcServerService(), PublicApplicationBean {

    // white grpc procedures
    override val whiteProcs = setOf(CK_VERIFY_JWT_PROCEDURE)

    val jwts by lazy { ConcurrentHashMap<String, JwtAttrs>() }

    @GrpcService
    inner class JwtVerifyService : AuthVerifyClientServiceGrpcKt.AuthVerifyClientServiceCoroutineImplBase(),
        PublicApplicationBean {
        override suspend fun verifyJwt(request: VerifyJwtRequest) =
            jwtVerifyGrpcService.verifyJwtInternal(request)

    }
}
