package org.dbs.goods.config

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.dbs.consts.RestHttpConsts.HTTP_200_STRING
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_STRING_TYPE
import org.dbs.goods.UsersConsts.CmQueryParams.QP_USER_LOGIN
import org.dbs.goods.UsersConsts.Routes.ROUTE_CREATE_OR_UPDATE_USER
import org.dbs.goods.UsersConsts.Routes.ROUTE_GET_USER_CREDENTIALS
import org.dbs.goods.UsersConsts.Routes.ROUTE_UPDATE_USER_PASSWORD
import org.dbs.goods.UsersConsts.Routes.ROUTE_UPDATE_USER_STATUS
import org.dbs.goods.UsersConsts.Routes.Tags.ROUTE_TAG_USER
import org.dbs.goods.dto.user.CreateOrUpdateUserRequest
import org.dbs.goods.dto.user.CreateUserResponse
import org.dbs.goods.dto.user.GetUserCredentialsResponse
import org.dbs.goods.dto.user.UpdateUserPasswordRequest
import org.dbs.goods.dto.user.UpdateUserPasswordResponse
import org.dbs.goods.dto.user.UpdateUserStatusRequest
import org.dbs.goods.dto.user.UpdateUserStatusResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
@RouterOperations(
    // Users
    // =================================================================================================================
    RouterOperation(
        path = ROUTE_CREATE_OR_UPDATE_USER,
        method = [POST],
        operation = Operation(
            description =
            """**Create/update new user**
            """,
            tags = [ROUTE_TAG_USER],
            operationId = ROUTE_CREATE_OR_UPDATE_USER,
            requestBody = RequestBody(
                description = """User attributes""",
                content = [Content(schema = Schema(implementation = CreateOrUpdateUserRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Create/update new user",
                    content = [Content(schema = Schema(implementation = CreateUserResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_UPDATE_USER_STATUS,
        method = [POST],
        operation = Operation(
            description =
            """**update user status**
            """,
            tags = [ROUTE_TAG_USER],
            operationId = ROUTE_UPDATE_USER_STATUS,
            requestBody = RequestBody(
                description = """new user status attributes""",
                content = [Content(schema = Schema(implementation = UpdateUserStatusRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "new user status attributes",
                    content = [Content(schema = Schema(implementation = UpdateUserStatusResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_UPDATE_USER_PASSWORD,
        method = [POST],
        operation = Operation(
            description =
            """**update user password**
            """,
            tags = [ROUTE_TAG_USER],
            operationId = ROUTE_UPDATE_USER_PASSWORD,
            requestBody = RequestBody(
                description = """new user password attributes""",
                content = [Content(schema = Schema(implementation = UpdateUserPasswordRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "new user password attributes",
                    content = [Content(schema = Schema(implementation = UpdateUserPasswordResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_GET_USER_CREDENTIALS,
        method = [GET],
        operation = Operation(
            description =
            """**Get user credentials**
            """,
            tags = [ROUTE_TAG_USER],
            operationId = ROUTE_GET_USER_CREDENTIALS,
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Get user credentials",
                    content = [Content(schema = Schema(implementation = GetUserCredentialsResponse::class))]
                )
            ],
            parameters = [
                Parameter(
                    `in` = QUERY,
                    name = QP_USER_LOGIN,
                    schema = Schema(type = QP_STRING_TYPE),
                    required = true,
                    description = "User login",
                    example = "User1",
                )]
        )
    ),
    // Lobbies
    // =================================================================================================================
//    RouterOperation(
//        path = ROUTE_CREATE_OR_UPDATE_LOBBY,
//        method = [POST],
//        operation = Operation(
//            description =
//            """**Create/update new lobby**
//            """,
//            tags = [ROUTE_TAG_LOBBY],
//            operationId = ROUTE_CREATE_OR_UPDATE_LOBBY,
//            requestBody = RequestBody(
//                description = """Lobby attributes""",
//                content = [Content(schema = Schema(implementation = CreateOrUpdateLobbyRequest::class))]
//            ),
//            responses = [
//                ApiResponse(
//                    responseCode = HTTP_200_STRING,
//                    description = "Create/update new lobby",
//                    content = [Content(schema = Schema(implementation = CreateLobbyResponse::class))]
//                )
//            ]
//        )
//    ),
//    RouterOperation(
//        path = ROUTE_UPDATE_LOBBY_STATUS,
//        method = [POST],
//        operation = Operation(
//            description =
//            """**update lobby status**
//            """,
//            tags = [ROUTE_TAG_LOBBY],
//            operationId = ROUTE_UPDATE_LOBBY_STATUS,
//            requestBody = RequestBody(
//                description = """new lobby status""",
//                content = [Content(schema = Schema(implementation = UpdateLobbyStatusRequest::class))]
//            ),
//            responses = [
//                ApiResponse(
//                    responseCode = HTTP_200_STRING,
//                    description = "new lobby status attributes",
//                    content = [Content(schema = Schema(implementation = UpdateLobbyStatusResponse::class))]
//                )
//            ]
//        )
//    ),
)
annotation class SwaggerOpenApiRoutesDefinitions
