package org.dbs.test.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.NoArg2Generic
import org.dbs.consts.NoArg2Unit
import org.dbs.utils.lateInitProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.system.measureTimeMillis


@Testcontainers
abstract class AbstractJUnitTest : Logging {

    @Value("\${reactive.rest.timeout:200000}")
    protected val timeoutDefault: Int = 200000

    private var environment : Environment by lateInitProperty()
    private var objectMapper : ObjectMapper by lateInitProperty()

    @Autowired
    fun initBeans(environment: Environment, objectMapper: ObjectMapper) {
        this.environment = environment
        this.objectMapper = objectMapper
    }

    //==========================================================================
    fun <T> runTestWithResult(testRunner: NoArg2Generic<T>) = run {
        logger.info("execute test ${testRunner.javaClass.simpleName}")

        val testResult: T
        measureTimeMillis {
            testResult = testRunner()
        }.also {
            logger.info("Test executed in $it ms")
        }
        testResult
    }

    fun runTest(testRunner: NoArg2Unit) = runTestWithResult(testRunner)

}
