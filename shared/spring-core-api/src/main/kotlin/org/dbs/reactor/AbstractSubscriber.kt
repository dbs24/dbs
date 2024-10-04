package org.dbs.reactor

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.spring.core.api.AbstractApplicationBean
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription


abstract class AbstractSubscriber<T> : AbstractApplicationBean(), Subscriber<T>, Logging {
    override fun onSubscribe(s: Subscription) {
        s.request(Long.MAX_VALUE)
        logger.debug("${this.javaClass} : ${s.javaClass}")
    }

    override fun onError(throwable: Throwable) {
        log(throwable) { "Abstract subscriber exception" }
    }

    override fun onComplete() {
        logger.debug("onComplete : ${this.javaClass}")
    }
}
