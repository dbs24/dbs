package org.dbs.sandbox.config

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.dbs.consts.RestHttpConsts
import org.dbs.invite.InviteConsts.Routes.ROUTE_CREATE_OR_UPDATE_INVITE
import org.dbs.invite.InviteConsts.Routes.Tags.ROUTE_TAG_INVITE
import org.dbs.invite.dto.invite.CreateInviteResponse
import org.dbs.invite.dto.invite.CreateOrUpdateInviteRequest
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.web.bind.annotation.RequestMethod
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
@RouterOperations(
    // Invites
    // =================================================================================================================
    RouterOperation(
        path = ROUTE_CREATE_OR_UPDATE_INVITE,
        method = [RequestMethod.POST],
        operation = Operation(
            description =
            """**Create/update new invite**
            """,
            tags = [ROUTE_TAG_INVITE],
            operationId = ROUTE_CREATE_OR_UPDATE_INVITE,
            requestBody = RequestBody(
                description = """Invite attributes""",
                content = [Content(schema = Schema(implementation = CreateOrUpdateInviteRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = RestHttpConsts.HTTP_200_STRING,
                    description = "Create/update new invite",
                    content = [Content(schema = Schema(implementation = CreateInviteResponse::class))]
                )
            ]
        )
    ),
)
annotation class SwaggerOpenApiRoutesDefinitions
