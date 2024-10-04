package org.dbs.analyst.config

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.dbs.consts.RestHttpConsts.HTTP_200_STRING
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_STRING_TYPE
import org.dbs.player.consts.PlayersConsts.CmQueryParams.QP_DEPTH
import org.dbs.player.consts.PlayersConsts.CmQueryParams.QP_FEN
import org.dbs.player.consts.PlayersConsts.CmQueryParams.QP_PLAYER_LOGIN
import org.dbs.player.consts.PlayersConsts.CmQueryParams.QP_TIMEOUT
import org.dbs.player.consts.PlayersConsts.CmQueryParams.QP_WHITE_MOVE
import org.dbs.player.consts.PlayersConsts.Routes.CM_GET_SOLUTION
import org.dbs.player.consts.PlayersConsts.Routes.ROUTE_CREATE_OR_UPDATE_PLAYER
import org.dbs.player.consts.PlayersConsts.Routes.ROUTE_GET_PLAYER_CREDENTIALS
import org.dbs.player.consts.PlayersConsts.Routes.Tags.ROUTE_TAG_PLAYER
import org.dbs.player.consts.PlayersConsts.Routes.Tags.ROUTE_TAG_SOLUTION
import org.dbs.player.dto.player.CreateOrUpdatePlayerRequest
import org.dbs.player.dto.player.CreatePlayerResponse
import org.dbs.player.dto.player.GetPlayerCredentialsResponse
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
            """**Create/update new school customer**
            """,
            tags = [ROUTE_TAG_PLAYER],
            operationId = ROUTE_CREATE_OR_UPDATE_PLAYER,
            requestBody = RequestBody(
                description = """School customer attributes""",
                content = [Content(schema = Schema(implementation = CreateOrUpdatePlayerRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Create/update new school customer",
                    content = [Content(schema = Schema(implementation = CreatePlayerResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_GET_PLAYER_CREDENTIALS,
        method = [GET],
        operation = Operation(
            description =
            """**Get school customers list**
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
    RouterOperation(
        path = CM_GET_SOLUTION,
        method = [GET],
        operation = Operation(
            description =
            """**Get fen solution**
            """,
            tags = [ROUTE_TAG_SOLUTION],
            operationId = CM_GET_SOLUTION,
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Get fen solution",
                    content = [Content(schema = Schema(implementation = GetPlayerCredentialsResponse::class))]
                )
            ],
            parameters = [
                Parameter(
                    `in` = QUERY,
                    name = QP_FEN,
                    schema = Schema(type = QP_STRING_TYPE),
                    required = true,
                    description = "fen solution",
                    example = "fen",
                ),
                Parameter(
                    `in` = QUERY,
                    name = QP_DEPTH,
                    schema = Schema(type = QP_STRING_TYPE),
                    required = true,
                    description = "depth",
                    example = "3",
                ),
                Parameter(
                    `in` = QUERY,
                    name = QP_TIMEOUT,
                    schema = Schema(type = QP_STRING_TYPE),
                    required = true,
                    description = "timeout",
                    example = "3",
                ),
                Parameter(
                    `in` = QUERY,
                    name = QP_WHITE_MOVE,
                    schema = Schema(type = QP_STRING_TYPE),
                    required = true,
                    description = "is white move",
                    example = "true",
                )]
        )
    ),
)
annotation class SwaggerOpenApiRoutesDefinitions
