package org.dbs.auth.verify.clients.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.dbs.auth.server.consts.AuthServerConsts.URI.ROUTE_JWT_VERIFY
import org.dbs.auth.verify.clients.auth.dto.VerifyJwtRequest
import org.dbs.auth.verify.clients.auth.dto.VerifyJwtResponse
import org.dbs.consts.RestHttpConsts.HTTP_200_STRING
import org.dbs.consts.SysConst.APP_JWT_VERIFY
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.web.bind.annotation.RequestMethod.POST
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
@RouterOperations(
    // Manager
    RouterOperation(
        path = ROUTE_JWT_VERIFY,
        method = [POST],
        operation = Operation(
            tags = [APP_JWT_VERIFY],
            operationId = ROUTE_JWT_VERIFY,
            requestBody = RequestBody(
                description = "jwt",
                content = [Content(schema = Schema(implementation = VerifyJwtRequest::class))]
            ),
            responses = [ApiResponse(
                responseCode = HTTP_200_STRING,
                description = "verify result",
                content = [Content(schema = Schema(implementation = VerifyJwtResponse::class))]
            )
            ]
        )
    )
)
annotation class SwaggerPlayerOpenApiRoutesDefinitions
