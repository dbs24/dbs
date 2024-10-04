package org.dbs.spring.core.api

import org.dbs.spring.core.api.ApplicationBean.Companion.findCanonicalService
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass


object ServiceLocator : Logging {
    private val applicationBeans = createCollection<ServiceBean>()
    fun registerService(applicationBean: ServiceBean) = applicationBeans.add(applicationBean)
    fun releaseService(applicationBean: ServiceBean) = applicationBeans.remove(applicationBean)

    @Suppress(UNCHECKED_CAST)
    fun <T : ServiceBean> findService(clazz: KClass<T>): T = applicationBeans
        .asSequence()
        .filter { srv -> srv == clazz || clazz.java.isAssignableFrom(srv.javaClass) }
        .firstOrNull()
        ?.let { it as T } ?: forceToLoadBean(clazz)

    private fun <T : ServiceBean> forceToLoadBean(clazz: KClass<T>): T = clazz.run {
        logger.warn { "try 2 load lazy bean ($this)" }
        findCanonicalService(clazz)
            .also { logger.debug { "bean '${it.javaClass.simpleName}' successfully loaded (hc=${it.hashCode()})" } }
    }
    @Suppress(UNCHECKED_CAST)
    fun <T: Any> ApplicationContext.findService(clazz: KClass<T>): T = getBean(clazz.java)
}
