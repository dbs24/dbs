package org.dbs.security.google

import org.dbs.application.core.service.funcs.StringFuncs.firstAndLast15
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_SECRET
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_TOKEN_RESPONSE
import org.dbs.consts.RestHttpConsts.URI_GOOGLE
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEV_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.GOOGLE_SERVER
import org.dbs.consts.SpringCoreConst.PropertiesNames.GOOGLE_SERVER_RECAPTCHA_V3_ROUTES_SITE_VERIFY
import org.dbs.consts.SpringCoreConst.PropertiesNames.GOOGLE_SERVER_RECAPTCHA_V3_SECRET
import org.dbs.consts.SysConst.NOT_DEFINED
import org.dbs.consts.SysConst.STRING_FALSE
import org.dbs.google.SiteVerifyResponse
import org.dbs.spring.core.api.AbstractWebClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class GoogleApiClientService : AbstractWebClientService() {

    @Value("\${$DEV_MODE:$STRING_FALSE}")
    private val devMode = false

    @Value("\${$GOOGLE_SERVER:$URI_GOOGLE}")
    private val googleServer = URI_GOOGLE

    @Value("\${$GOOGLE_SERVER_RECAPTCHA_V3_SECRET:$NOT_DEFINED}")
    private val secretV3 = NOT_DEFINED

    @Value("\${$GOOGLE_SERVER_RECAPTCHA_V3_ROUTES_SITE_VERIFY:/recaptcha/api/siteverify}")
    private val siteVerifyUrl = NOT_DEFINED

    override fun initialize() = super.initialize().also { prepareDefaultWebClient(googleServer) }

    fun verifyUserResponse(responseToken: String): Mono<Boolean> = run {
        if (devMode) true.toMono() else
            webClientExecute(siteVerifyUrl, Void::class.java, SiteVerifyResponse::class.java, {
                logger.debug { "$googleServer$siteVerifyUrl[secretKey=${secretV3.last15()}, responseToken=${responseToken.firstAndLast15()}]" }
                it.queryParam(QP_SECRET, secretV3)
                    .queryParam(QP_TOKEN_RESPONSE, responseToken)
            }).map {
                logger.debug { "$googleServer$siteVerifyUrl: [$it]" }
                it.success
            }
    }
}
