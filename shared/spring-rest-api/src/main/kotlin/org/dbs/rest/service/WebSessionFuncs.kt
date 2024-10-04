package org.dbs.rest.service

import org.apache.logging.log4j.kotlin.KotlinLogger
import org.springframework.web.server.WebSession
import reactor.core.Disposable
import reactor.core.publisher.Mono

object WebSessionFuncs {
    fun WebSession.log() = let {
        "WebSession($id)[attributes=$attributes, creationTime=$creationTime, lastAccessTime=$lastAccessTime, " +
                "maxIdleTime=$maxIdleTime]"
    }
    fun Mono<WebSession>.logAndInvalidate(logger: KotlinLogger): Disposable = flatMap {
        it.also { logger.warn { "invalidate ${it.log()}" } }.invalidate()
    }.subscribe()
}
