package org.dbs.test.core

import org.apache.logging.log4j.kotlin.Logging
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.GenericContainer

abstract class AbstractTestContainer2 : Logging {

    private val container: GenericContainer<*> = let {
        logger.debug { "create container '${javaClass.canonicalName}')" }
        createContainer().also { logger.debug { "createdContainer: $it " } }
    }

    init {
        container.start()
        require(container.isRunning) { "container '${javaClass::class.java.canonicalName}' is not running" }
    }

    abstract fun <T : GenericContainer<*>> createContainer(): T

    abstract fun overrideApplicationProperties(registry: DynamicPropertyRegistry)
}
