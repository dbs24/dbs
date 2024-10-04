package org.dbs.spring.core.api

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.exception.InternalAppException.Companion.getSuppressedErrMessage
import org.dbs.application.core.service.funcs.TestFuncs.generateTestInteger
import org.dbs.application.core.service.funcs.ThrowableFuncs.getExtendedErrMessage
import org.dbs.consts.ErrMsg
import org.dbs.consts.NoArg2Generic
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_DOMAIN
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.spring.core.api.ApplicationBean.Companion.externalAddresses
import org.dbs.spring.core.api.liveness.LivenessHost
import org.dbs.spring.core.api.liveness.TrackingHost
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import java.net.URI
import kotlin.random.Random

abstract class AbstractApplicationBean : ApplicationBean, ApplicationEventPublisherAware {

    val random by lazy { Random }
    val tp = ThrowableProcessor { th: Throwable ->

        logger.error("### Exception: '${th.message}'")
        //th.printStackTrace() ?: run { logger.error("### throwable object is invalid") }

    }
    private val eventPublisher by lazy { LateInitVal<ApplicationEventPublisher>() }

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        applicationEventPublisher.also {
            eventPublisher.init(it)
            //logger.debug {"initialize applicationEventPublisher (${applicationEventPublisher::class.java.canonicalName})"}
            logger.trace { "initialize applicationEventPublisher for (${this::class.java.canonicalName})" }
        }
    }

    fun log(throwable: Throwable, errMsg: NoArg2Generic<ErrMsg?>) = throwable.apply {
        val excName = errMsg() ?: "generic message"
        val errorId = generateTestInteger()
        logger.error("E${errorId}; $excName: '${this.getExtendedErrMessage()}'")
        logger.error("E${errorId}; $excName: '${getSuppressedErrMessage(this)}'")
        printStackTrace()
    }

    protected fun buildDefaultErrMsg(throwable: Throwable) =
        "Unexpected error: see the fucked log4j for details: ${throwable.javaClass.simpleName}: ${throwable.message}"

    fun log(th: Throwable) = log(th) { STRING_NULL }

    fun addHost4LivenessTracking(host: String, port: Int, serviceName: String) = host.apply {
        val actualPort = port.let { if (it > 0) it else 443 }
        if (host != URI_LOCALHOST_DOMAIN) {
            logger.debug { "register host 4 liveness tracking: [$host:$actualPort]" }
            externalAddresses.add(LivenessHost(TrackingHost(host, actualPort, serviceName)))
        }
    }

    fun addUrl4LivenessTracking(uri: String, serviceName: String) = runCatching {
        URI(uri).toURL().let {
            logger.debug { "prepare host 4 tracking: $it" }

            val actualHost = it.host
            val actualPort = it.port.let { if (it > 0) it else 443 }

            addHost4LivenessTracking(actualHost, actualPort, serviceName)
        }
    }.getOrElse {
        logger.error { "addUrl4LivenessTracking: $it" }
    }
}
