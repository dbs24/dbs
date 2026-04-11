package org.dbs.spring.core.api

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.spring.core.api.ApplicationBean.Companion.findCanonicalService
import org.springframework.context.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object ServiceLocator : Logging {

    private val cache by lazy { ConcurrentHashMap<String, ServiceBean>() }

    fun <T : ServiceBean> findService(clazz: KClass<T>): T {
        val bean = cache.getOrPut(clazz.java.name) {
            forceToLoadBean(clazz)
        }

        return clazz.java.cast(bean)
    }

    private fun <T : ServiceBean> forceToLoadBean(clazz: KClass<T>): T = clazz.run {
        logger.warn { "try 2 load lazy bean ($this)" }
        findCanonicalService(clazz)
            .also { logger.debug { "bean '${it.javaClass.simpleName}' successfully loaded (hc=${it.hashCode()})" } }
    }
    @Suppress(UNCHECKED_CAST)
    fun <T: Any> ApplicationContext.findService(clazz: KClass<T>): T = getBean(clazz.java)
}
