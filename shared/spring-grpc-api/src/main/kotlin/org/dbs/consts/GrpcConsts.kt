package org.dbs.consts

import io.grpc.Context
import io.grpc.Metadata
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import io.jsonwebtoken.Claims
import kotlinx.coroutines.CoroutineScope
import org.dbs.consts.SpringCoreConst.DELIMITER
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_ENABLED_REFLECTION
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_SERVER_CERT
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_SERVER_CERT_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_SERVER_PORT
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_SERVER_SECURITY
import org.dbs.consts.SpringCoreConst.PropertiesNames.NOT_AVAILABLE
import org.dbs.consts.SysConst.EMPTY_STRING

typealias MK = Metadata.Key<String>
typealias CTX = Context
typealias CKC = Context.Key<Claims>
typealias CKS = Context.Key<String>
typealias CKI = Context.Key<EntityId>

object GrpcConsts {
    val GRPC_SET: StringMap = mapOf(
        "GRPC server port" to GRPC_SERVER_PORT,
        "GRPC server reflection" to GRPC_ENABLED_REFLECTION,
        "GRPC SSL enabled" to GRPC_SERVER_SECURITY,
        "GRPC cert" to "$GRPC_SERVER_CERT:$NOT_AVAILABLE",
        "GRPC cert key" to "$GRPC_SERVER_CERT_KEY:$NOT_AVAILABLE",
        DELIMITER to EMPTY_STRING
    )

    object MetadataKeys {
        val GRPC_IS_AUTHORIZED: MK = MK.of("IS_AUTHORIZED", ASCII_STRING_MARSHALLER)
        val GRPC_BEARER_AUTHORIZATION: MK = MK.of("AUTHORIZATION", ASCII_STRING_MARSHALLER)
        val JWT_AUTHORIZATION_FAIL: MK = MK.of("JWT_AUTHORIZATION_FAIL", ASCII_STRING_MARSHALLER)
        val MISSING_SERVICE_JWT: MK = MK.of("MISSING_SERVICE_JWT", ASCII_STRING_MARSHALLER)
        val INVALID_JWT: MK = MK.of("INVALID_JWT", ASCII_STRING_MARSHALLER)
        val X_REAL_IP: MK = MK.of("x-real-ip", ASCII_STRING_MARSHALLER)
        val GRPC_USER_AGENT: MK = MK.of("user-agent", ASCII_STRING_MARSHALLER)
        val GRPC_ABUSED_CONNECTION: MK = MK.of("GRPC_ABUSED_CONNECTION", ASCII_STRING_MARSHALLER)
    }

    object ContextKeys {
        // grpc reflection
        val CK_SERVER_REFLECTION_INFO: CKS = CTX.key("ServerReflectionInfo")
        val CK_REMOTE_ADDRESS: CKS = CTX.key("REMOTE_ADDRESS")
        val CK_USER_AGENT: CKS = CTX.key("USER_AGENT")
        val CK_GRPC_CONTEXT: CKS = CTX.key("GRPC_CONTEXT")
        val CK_INTERCEPTOR_EXCEPTION: CKS = CTX.key("INTERCEPTOR_EXCEPTION")
        val CK_GRPC_PROCEDURE_NAME: CKS = CTX.key("GRPC_PROCEDURE_NAME")
        val CK_JWT_CLAIMS: CKC = CTX.key("JWT_CLAIMS")
    }

    object Refletion {
        const val builderMethod = "newBuilder"
        const val responseAnswerMethod = "setResponseAnswer"
        const val buildMethod = "build"
    }

    object Coroutines {
        const val HEAVY_SPEED_LIMIT_MS = 100
        const val MIN_SPEED_LIMIT = 5
    }
}
