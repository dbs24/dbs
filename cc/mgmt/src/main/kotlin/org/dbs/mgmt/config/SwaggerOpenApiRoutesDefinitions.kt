package org.dbs.mgmt.config

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.dbs.consts.RestHttpConsts.HTTP_200_STRING
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_STRING_TYPE
import org.dbs.player.PlayersConsts.CmQueryParams.QP_PLAYER_LOGIN
import org.dbs.player.PlayersConsts.Routes.ROUTE_CREATE_OR_UPDATE_PLAYER
import org.dbs.player.PlayersConsts.Routes.ROUTE_GET_PLAYER_CREDENTIALS
import org.dbs.player.PlayersConsts.Routes.ROUTE_UPDATE_PLAYER_PASSWORD
import org.dbs.player.PlayersConsts.Routes.ROUTE_UPDATE_PLAYER_STATUS
import org.dbs.player.PlayersConsts.Routes.Tags.ROUTE_TAG_PLAYER
import org.dbs.player.dto.player.CreateOrUpdatePlayerRequest
import org.dbs.player.dto.player.CreatePlayerResponse
import org.dbs.player.dto.player.GetPlayerCredentialsResponse
import org.dbs.player.dto.player.UpdatePlayerPasswordRequest
import org.dbs.player.dto.player.UpdatePlayerPasswordResponse
import org.dbs.player.dto.player.UpdatePlayerStatusRequest
import org.dbs.player.dto.player.UpdatePlayerStatusResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
@RouterOperations(
    // Players
    // =================================================================================================================
    RouterOperation(
        path = ROUTE_CREATE_OR_UPDATE_PLAYER,
        method = [POST],
        operation = Operation(
            description =
            """**Create/update new player**
            """,
            tags = [ROUTE_TAG_PLAYER],
            operationId = ROUTE_CREATE_OR_UPDATE_PLAYER,
            requestBody = RequestBody(
                description = """Player attributes""",
                content = [Content(schema = Schema(implementation = CreateOrUpdatePlayerRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Create/update new player",
                    content = [Content(schema = Schema(implementation = CreatePlayerResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_UPDATE_PLAYER_STATUS,
        method = [POST],
        operation = Operation(
            description =
            """**update player status**
            """,
            tags = [ROUTE_TAG_PLAYER],
            operationId = ROUTE_UPDATE_PLAYER_STATUS,
            requestBody = RequestBody(
                description = """new player status attributes""",
                content = [Content(schema = Schema(implementation = UpdatePlayerStatusRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "new player status attributes",
                    content = [Content(schema = Schema(implementation = UpdatePlayerStatusResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_UPDATE_PLAYER_PASSWORD,
        method = [POST],
        operation = Operation(
            description =
            """**update player password**
            """,
            tags = [ROUTE_TAG_PLAYER],
            operationId = ROUTE_UPDATE_PLAYER_PASSWORD,
            requestBody = RequestBody(
                description = """new player password attributes""",
                content = [Content(schema = Schema(implementation = UpdatePlayerPasswordRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "new player password attributes",
                    content = [Content(schema = Schema(implementation = UpdatePlayerPasswordResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_GET_PLAYER_CREDENTIALS,
        method = [GET],
        operation = Operation(
            description =
            """**Get player credentials**
            """,
            tags = [ROUTE_TAG_PLAYER],
            operationId = ROUTE_GET_PLAYER_CREDENTIALS,
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Get player credentials",
                    content = [Content(schema = Schema(implementation = GetPlayerCredentialsResponse::class))]
                )
            ],
            parameters = [
                Parameter(
                    `in` = QUERY,
                    name = QP_PLAYER_LOGIN,
                    schema = Schema(type = QP_STRING_TYPE),
                    required = true,
                    description = "Player login",
                    example = "Player1",
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
