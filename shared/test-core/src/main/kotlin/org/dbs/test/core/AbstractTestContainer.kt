package org.dbs.test.core

import org.apache.logging.log4j.kotlin.Logging
import org.springframework.test.context.DynamicPropertyRegistry

abstract class AbstractTestContainer : Logging {
    abstract fun overrideApplicationProperties(registry: DynamicPropertyRegistry)
}
