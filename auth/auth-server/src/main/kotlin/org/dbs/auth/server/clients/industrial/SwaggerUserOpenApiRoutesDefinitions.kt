package org.dbs.auth.server.clients.industrial

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.dbs.auth.server.consts.AuthServerConsts.URI.ROUTE_USER_LOGIN
import org.dbs.auth.server.consts.AuthServerConsts.URI.ROUTE_USER_REFRESH_JWT
import org.dbs.consts.RestHttpConsts.HTTP_200_STRING
import org.dbs.consts.SysConst.APP_CHESS_COMMUNITY
import org.dbs.rest.dto.value.LoginUserRequest
import org.dbs.rest.dto.value.LoginUserResponse
import org.dbs.rest.dto.value.RefreshJwtRequest
import org.dbs.rest.dto.value.RefreshJwtResponse
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
        path = ROUTE_USER_LOGIN,
        method = [POST],
        operation = Operation(
            tags = [APP_CHESS_COMMUNITY],
            operationId = ROUTE_USER_LOGIN,
            requestBody = RequestBody(
                description = "user login request details",
                content = [Content(schema = Schema(implementation = LoginUserRequest::class))]
            ),
            responses = [ApiResponse(
                responseCode = HTTP_200_STRING,
                description = "login user and create jwt token",
                content = [Content(schema = Schema(implementation = LoginUserResponse::class))]
            )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_USER_REFRESH_JWT,
        method = [POST],
        operation = Operation(
            tags = [APP_CHESS_COMMUNITY],
            operationId = ROUTE_USER_REFRESH_JWT,
            requestBody = RequestBody(
                description = "refresh user jwt request details",
                content = [Content(schema = Schema(implementation = RefreshJwtRequest::class))]
            ),
            responses = [ApiResponse(
                responseCode = HTTP_200_STRING,
                description = "refresh user jwt request details",
                content = [Content(schema = Schema(implementation = RefreshJwtResponse::class))]
            )
            ]
        )
    )
)
annotation class SwaggerUserOpenApiRoutesDefinitions
