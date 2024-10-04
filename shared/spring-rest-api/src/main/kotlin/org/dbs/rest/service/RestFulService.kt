package org.dbs.rest.service

import io.jsonwebtoken.Claims
import kotlinx.coroutines.runBlocking
import org.dbs.application.core.nullsafe.StopWatcher
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.*
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_RESTFUL_MESSAGE_PRINT_ENTITY_ID
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEFAULT_SYS_CURRENCY
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEFAULT_SYS_CURRENCY_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAX_PAGES
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAX_PAGES_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAX_PAGE_SIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAX_PAGE_SIZE_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.QUERY_MAX_EXEC_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.QUERY_MAX_EXEC_TIME_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_RESTFUL_MESSAGE_PRINT_ENTITY_ID
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.ext.LoggerFuncs.logRequestInternal
import org.dbs.ref.serv.enums.CurrencyEnum.USD
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.spring.security.api.JwtSecurityServiceApi
import org.dbs.validator.exception.EmptyBodyException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.function.Consumer
import kotlin.reflect.KClass


@Service
class RestFulService : AbstractApplicationService() {

    @Value("\${$CONFIG_RESTFUL_MESSAGE_PRINT_ENTITY_ID:$VALUE_RESTFUL_MESSAGE_PRINT_ENTITY_ID}")
    private val printEntityId = false

    @Value("\${$QUERY_MAX_EXEC_TIME:$QUERY_MAX_EXEC_TIME_VALUE}")
    private val queryMaxTimeExec = QUERY_MAX_EXEC_TIME_VALUE

    @Value("\${$MAX_PAGE_SIZE:$MAX_PAGE_SIZE_VALUE}")
    val maxPageSize = MAX_PAGE_SIZE_VALUE

    @Value("\${$MAX_PAGES:$MAX_PAGES_VALUE}")
    val maxPages = MAX_PAGES_VALUE

    @Value("\${$DEFAULT_SYS_CURRENCY:$DEFAULT_SYS_CURRENCY_VALUE}")
    val storeMainCurrency = USD

    @Suppress(UNCHECKED_CAST)
    fun <REQ : RequestDto, R : AbstractHttpRequestBody<REQ>, RESP : ResponseDto, T : HttpResponseBody<RESP>>
            buildMonoResponse(
        serverRequest: ServerRequest,
        classR: KClass<R>,
        funkResponseBody: SuspendArg2Mono<REQ, T>,
    ): Mono<T> = serverRequest.run {
        val path = method().name().plus(path())

        bodyToMono(classR.java)
            .switchIfEmpty {
                if (method().name() == "POST") {
                    Mono.error(EmptyBodyException("post query has empty body"))
                } else {
                    empty()
                }
            }
            .flatMap { requestBody ->
                val stopWatcher = StopWatcher.create()
                logger.debug("*** $path: [$requestBody]")
                runBlocking {  funkResponseBody(requestBody.requestBodyDto) }
                    //.switchIfEmpty{Mono.defer { responseBody.toMono() })
                    .map { finishResponse(it, path, stopWatcher.executionTime) }
                    .doOn()
            }
    }

    @Suppress(UNCHECKED_CAST)
    suspend fun <RESP : ResponseDto, T : HttpResponseBody<RESP>> buildMonoResponse(
        serverRequest: ServerRequest,
        funkResponseBody: SuspendNoArg2Mono<T>,
    ): Mono<T> = serverRequest.run {
        val path = method().name().plus(path())
        val stopWatcher = StopWatcher.create()
        funkResponseBody()
            .map { finishResponse(it, path, stopWatcher.executionTime) }
            .doOn()
    }

    fun getJwtClaim(jwt: Jwt, claimName: String): String = jwt.run {
        jwtSecurityService.getClaim(this, claimName) ?: error("claim is not found ($claimName), jwt=[${this.last15()}]")
    }

    fun getJwtClaims(jwt: Jwt): Claims = jwt.run {
        jwtSecurityService.getAllClaimsFromJwt(this)
    }

    private fun <T> Mono<T>.doOn() = this.doOnError(throwableConsumer)

    private fun <RESP : ResponseDto, T : HttpResponseBody<RESP>> finishResponse(
        responseBody: T,
        path: String,
        execMillis: Long,
    ): T = responseBody.run { finishResponseApply(this, path, execMillis) }

    private fun <RESP : ResponseDto, T : HttpResponseBody<RESP>> finishResponseApply(
        responseBody: T,
        path: String,
        execMillis: Long,
    ) = responseBody.apply {
        execMillis.also {
            execTimeMillis = it.toInt().run { if (this == 0) 1 else this }
            logger.logRequestInternal(it, queryMaxTimeExec) { "[$path]" }
            logger.debug { "▒▒▒ [h1, $path: ${responseBody.toString2()}]" }
        }

        if (errors.isNotEmpty()) {
            error = error ?: errors.first().errorMsg
            message = error ?: "error"

            val singleError = (errorsCount == 1)

            logger.warn(
                "### ERROR PROCESSING '${path.uppercase()}' - there ${if (singleError) "is" else "are"} " +
                        "{$errorsCount} error${if (!singleError) "s" else ""}: {$errors}"
            )
        }

        message.run { this.ifEmpty { STRING_NULL } } ?: run {
            logger.warn("### ${path.uppercase()}: response message is null or empty".uppercase())
        }
    }

    private val throwableConsumer = Consumer { throwable: Throwable ->
        logger.error("### {$throwable.cause}: {$throwable.message}")
        logger.error("### MonoResponse exception: {$throwable.javaClass.simpleName}-'${throwable}'")
    }

    //==========================================================================

    protected fun foundStdEntityServiceMsg(clazz: Class<*>, entityId: EntityId?, extMessage: String?) =
        "${clazz.simpleName}: ${entityId?.let { "found" } ?: "create new"} " +
                "entity ${printEntityId(entityId)} $extMessage"

    fun foundStdEntityServiceMsg(clazz: Class<*>, entityCode: String) =
        "${clazz.simpleName}: found entity '$entityCode'"

    fun foundStdEntitiesServiceMsg(clazz: Class<*>, entitiesCount: Int) =
        "${clazz.simpleName}: found entities - $entitiesCount"

    private fun printEntityId(entityId: EntityId?) =
        if (printEntityId) "(entityId: ${entityId ?: EMPTY_STRING})" else EMPTY_STRING

    companion object {
        private val jwtSecurityService by lazy { findService(JwtSecurityServiceApi::class) }
    }
}