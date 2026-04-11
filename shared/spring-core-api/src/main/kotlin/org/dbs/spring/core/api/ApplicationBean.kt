package org.dbs.spring.core.api

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.application.core.service.funcs.StringFuncs.clearName
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.spring.core.api.liveness.LivenessHost
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

sealed interface ApplicationBean : InitializingBean, DisposableBean, Logging, ServiceBean {
    fun initialize() = logger.debug { "Service '${javaClass.simpleName.clearName()}' was created" }

    fun shutdown() {
        TODO("not implemented")
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        initialize()
    }

    @Throws(Exception::class)
    override fun destroy() {
        logger.trace { "Service '${javaClass.simpleName.clearName()}' is destroyed" }
        shutdown()
    }

    @Autowired
    fun initializeApplicationContext(context: ApplicationContext) {
        applicationContext = context
    }

    companion object {

        val externalAddresses by lazy { createCollection<LivenessHost>() }
        private lateinit var applicationContext: ApplicationContext
        fun <T : Any> findCanonicalService(clazz: KClass<T>) = applicationContext.findService(clazz)
    }
}
