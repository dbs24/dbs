package org.dbs.outOfService.config

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.dbs.consts.RestHttpConsts.HTTP_200_STRING
import org.dbs.outOfService.consts.OutOfServiceConsts.Routes.ROUTE_CREATE_OR_UPDATE_CORE_OUT_OF_SERVICE
import org.dbs.outOfService.consts.OutOfServiceConsts.Routes.ROUTE_GET_CORE_OUT_OF_SERVICE
import org.dbs.outOfService.consts.OutOfServiceConsts.Routes.Tags.ROUTE_TAG_CORE_OUT_OF_SERVICE
import org.dbs.outOfService.dto.CreateOrUpdateCoreOutOfServiceRequest
import org.dbs.outOfService.dto.CreateOrUpdateCoreOutOfServiceResponse
import org.dbs.outOfService.dto.GetCoreOutOfServiceResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestMethod.GET
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
@RouterOperations(
    // Core out of service
    // =================================================================================================================
    RouterOperation(
        path = ROUTE_CREATE_OR_UPDATE_CORE_OUT_OF_SERVICE,
        method = [POST],
        operation = Operation(
            description =
            """**Create or update core out of service**
            """,
            tags = [ROUTE_TAG_CORE_OUT_OF_SERVICE],
            operationId = ROUTE_CREATE_OR_UPDATE_CORE_OUT_OF_SERVICE,
            requestBody = RequestBody(
                description = """Create or update core out of service""",
                content = [Content(schema = Schema(implementation = CreateOrUpdateCoreOutOfServiceRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Create or update core out of service",
                    content = [Content(schema = Schema(implementation = CreateOrUpdateCoreOutOfServiceResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_GET_CORE_OUT_OF_SERVICE,
        method = [GET],
        operation = Operation(
            description =
            """**Get core out of service**
            """,
            tags = [ROUTE_TAG_CORE_OUT_OF_SERVICE],
            operationId = ROUTE_GET_CORE_OUT_OF_SERVICE,
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Get core out of service",
                    content = [Content(schema = Schema(implementation = GetCoreOutOfServiceResponse::class))]
                )
            ]
        )
    ),
)
annotation class SwaggerOpenApiRoutesDefinitions
