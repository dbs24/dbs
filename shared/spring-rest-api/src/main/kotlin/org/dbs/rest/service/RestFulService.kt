package org.dbs.rest.service

import io.jsonwebtoken.Claims
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.dbs.application.core.nullsafe.StopWatcher
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.EntityId
import org.dbs.consts.Jwt
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
import org.dbs.consts.SuspendArg2Mono
import org.dbs.consts.SuspendNoArg2Mono
import org.dbs.consts.SysConst.EMPTY_STRING
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
import java.util.function.Consumer
import kotlin.reflect.KClass


@Service
class RestFulService : AbstractApplicationService() {

    @Value("\${$CONFIG_RESTFUL_MESSAGE_PRINT_ENTITY_ID:$VALUE_RESTFUL_MESSAGE_PRINT_ENTITY_ID}")
    private val printEntityIdProp = false

    @Value("\${$QUERY_MAX_EXEC_TIME:$QUERY_MAX_EXEC_TIME_VALUE}")
    private val queryMaxTimeExecProp = QUERY_MAX_EXEC_TIME_VALUE

    @Value("\${$MAX_PAGE_SIZE:$MAX_PAGE_SIZE_VALUE}")
    private val maxPageSizeProp = MAX_PAGE_SIZE_VALUE

    @Value("\${$MAX_PAGES:$MAX_PAGES_VALUE}")
    private val maxPagesProp = MAX_PAGES_VALUE

    @Value("\${$DEFAULT_SYS_CURRENCY:$DEFAULT_SYS_CURRENCY_VALUE}")
    private val storeMainCurrencyProp = USD

    // Lazy initialization as per requirements
    private val printEntityId by lazy { printEntityIdProp }
    private val queryMaxTimeExec by lazy { queryMaxTimeExecProp }
    val maxPageSize by lazy { maxPageSizeProp }
    val maxPages by lazy { maxPagesProp }
    val storeMainCurrency by lazy { storeMainCurrencyProp }

    @Suppress(UNCHECKED_CAST)
    fun <REQ : RequestDto, R : AbstractHttpRequestBody<REQ>, RESP : ResponseDto, T : HttpResponseBody<RESP>>
            buildMonoResponse(
        serverRequest: ServerRequest,
        classR: KClass<R>,
        funkResponseBody: SuspendArg2Mono<REQ, T>,
    ): Mono<T> = serverRequest.run {
        val path = method().name().plus(path())

        bodyToMono(classR.java)
            .switchIfEmpty (
                method().name().takeIf { it == "POST" }
                    ?.run { Mono.error(EmptyBodyException("post query has empty body")) }
                    ?: Mono.empty()
            )
            .flatMap { requestBody ->
                val stopWatcher = StopWatcher.create()
                logger.debug("*** $path: [$requestBody]")
                runBlocking(Dispatchers.IO) { funkResponseBody(requestBody.requestBodyDto) }
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

    fun getJwtClaim(jwt: Jwt, claimName: String): String =
        jwtSecurityService.getClaim(jwt, claimName) ?: error("claim is not found ($claimName), jwt=[${jwt.last15()}]")

    fun getJwtClaims(jwt: Jwt): Claims =
        jwtSecurityService.getAllClaimsFromJwt(jwt)

    private fun <T: Any> Mono<T>.doOn() = this.doOnError(throwableConsumer)

    private fun <RESP : ResponseDto, T : HttpResponseBody<RESP>> finishResponse(
        responseBody: T,
        path: String,
        execMillis: Long,
    ): T = finishResponseApply(responseBody, path, execMillis)

    private fun <RESP : ResponseDto, T : HttpResponseBody<RESP>> finishResponseApply(
        responseBody: T,
        path: String,
        execMillis: Long,
    ) = responseBody.apply {
        execTimeMillis = execMillis.toInt().takeUnless { it == 0 } ?: 1
        logger.logRequestInternal(execMillis, queryMaxTimeExec) { "[$path]" }
        logger.debug { "▒▒▒ [h1, $path: ${toString2()}]" }

        errors.takeIf { it.isNotEmpty() }?.run {
            message = "error"

            val isSingle = (errorsCount == 1)
            logger.warn(
                "### ERROR PROCESSING '${path.uppercase()}' - there ${if (isSingle) "is" else "are"} " +
                        "{$errorsCount} error${if (!isSingle) "s" else ""}: {$errors}"
            )
        }

        message.takeIf { it.isNotEmpty() } ?: run {
            logger.warn("### ${path.uppercase()}: response message is null or empty".uppercase())
        }
    }

    private val throwableConsumer by lazy {
        Consumer<Throwable> { t ->
            logger.error("### {${t.cause}}: {${t.message}}")
            logger.error("### MonoResponse exception: {${t.javaClass.simpleName}}-'$t'")
        }
    }

    //==========================================================================

    fun foundStdEntityServiceMsg(clazz: Class<*>, entityCode: String) =
        "${clazz.simpleName}: found entity '$entityCode'"

    fun foundStdEntitiesServiceMsg(clazz: Class<*>, entitiesCount: Int) =
        "${clazz.simpleName}: found entities - $entitiesCount"

    private fun printEntityId(entityId: EntityId?) =
        printEntityId.takeIf { it }?.run { "(entityId: ${entityId ?: EMPTY_STRING})" } ?: EMPTY_STRING

    companion object {
        private val jwtSecurityService by lazy { findService(JwtSecurityServiceApi::class) }
    }
}
