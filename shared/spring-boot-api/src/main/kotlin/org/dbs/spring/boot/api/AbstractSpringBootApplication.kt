package org.dbs.spring.boot.api

import com.sun.management.HotSpotDiagnosticMXBean
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.nullsafe.StopWatcher
import org.dbs.application.core.nullsafe.StopWatcher.Companion.defaultZoneId
import org.dbs.application.core.service.funcs.GetNetworkAddress
import org.dbs.application.core.service.funcs.GetNetworkAddress.currentHostName
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toString2
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.application.core.service.funcs.StringFuncs.clearName
import org.dbs.application.core.service.funcs.StringFuncs.secureReplace
import org.dbs.application.core.service.funcs.SysEnvFuncs.appCreateTime
import org.dbs.application.core.service.funcs.SysEnvFuncs.memoryStatistics
import org.dbs.application.core.service.funcs.SysEnvFuncs.processHandleInfo
import org.dbs.consts.SpringCoreConst.App.BUFFER_APP_SIZE
import org.dbs.consts.SpringCoreConst.App.STOPPED
import org.dbs.consts.SpringCoreConst.App.SUCCESSFULLY_STARTED
import org.dbs.consts.SpringCoreConst.PropertiesNames.BANNER_ROW_BOLD_DELIMITER
import org.dbs.consts.SpringCoreConst.PropertiesNames.JUNIT_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_ACTIVE_PROFILE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_DEVTOOLS_RESTART_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.USER_TIME_ZONE
import org.dbs.consts.SysConst.APP_SHUTDOWN_DELAY
import org.dbs.consts.SysConst.DEF_EXIT_PROCESS
import org.dbs.consts.SysConst.HotSpotConsts.VMS
import org.dbs.consts.SysConst.KOTLIN_VERSION
import org.dbs.consts.SysConst.MN42
import org.dbs.consts.SysConst.MN42L
import org.dbs.consts.SysConst.SB_DEF_CAPACITY
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.ext.LoggerFuncs.initLog4j2
import org.dbs.spring.boot.banner.BannerBuilder
import org.dbs.spring.boot.banner.BannerInititializer
import org.dbs.spring.boot.vm.WmBuilder
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.boot.info.BuildProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.support.GenericApplicationContext
import reactor.kotlin.core.publisher.toMono
import reactor.tools.agent.ReactorDebugAgent
import java.lang.System.getProperty
import java.lang.System.setProperty
import java.lang.Thread.sleep
import java.lang.management.ManagementFactory
import java.util.*
import java.util.stream.Stream
import kotlin.reflect.KClass
import kotlin.system.exitProcess


abstract class AbstractSpringBootApplication : Logging {

    @Value("\${config.security.secured-params:password,username}")
    private val securedParams = "password,username"

    private fun run(args: Array<String>) {
        if (args.isNotEmpty()) {
            logger.debug {
                Stream.of(*args)
                    .reduce(
                        """
    Modified application arguments   
    """.trimIndent()
                    ) { x: String, y: String ->
                        x + " * '${y.secureReplace(securedParams)}'\n"
                    }
            }
        }

        val applicationName = this.javaClass.canonicalName
        logger.info { "Application is validated '${applicationName.clearName()}'" }
    }

    private class SpringBootSubscriber<T : Any>(
        val applicationName: String,
        val args: Array<String>,
        val sbi: SpringBootInititializer,
        val bi: BannerInititializer,
    ) : Subscriber<KClass<T>>, Logging, DisposableBean {
        val stopWatcher = StopWatcher()

        override fun onSubscribe(s: Subscription) {
            s.request(MN42L)
            logger.info { "Initializing $applicationName ... " }
            logger.info { "$processHandleInfo " }
            logger.info { "logger.isDebugEnabled = ${logger.delegate.isDebugEnabled} " }
            logger.info { "logger.isTraceEnabled = ${logger.delegate.isTraceEnabled} " }
        }

        override fun onNext(clazz: KClass<T>) {
            logger.debug { "Initializing Spring Boot application ${clazz.simpleName} " }

            createDefaultBanner(clazz)

            SpringApplication(clazz.java).apply {
                applicationStartup = BufferingApplicationStartup(BUFFER_APP_SIZE)
                applicationStartup.start(clazz.simpleName ?: UNKNOWN)
                run(*args)
                logger.initLog4j2(userApplicationName.value)
            }

            sbi.initialize()
            logger.debug { "${GetNetworkAddress.allAddresses}, port $serverPort" }

            isRunning = true
            val appMsg = "$applicationName, $currentHostName, $GetNetworkAddress.allAddresses, port = $serverPort"

            logger.info { appMsg }
            logger.info { SUCCESSFULLY_STARTED }
            logger.info { KOTLIN_VERSION }
            genericApplicationContext.displayName = clazz.simpleName ?: clazz.toString()

            Runtime.getRuntime().addShutdownHook(
                Thread {
                    logger.info { appMsg }
                    logger.info { "$processHandleInfo " }
                    logger.info { "$applicationName: server shut down" }
                }
            )
        }

        //--------------------------------------------------------------------------------------------------------------
        private fun createDefaultBanner(clazz: KClass<T>) = BannerBuilder(bi.initialize()).build(clazz)

        //--------------------------------------------------------------------------------------------------------------

        override fun onError(t: Throwable) {
            logger.error {
                "ERROR STARTING '${applicationName}' ('$userApplicationName'), " +
                        "reason: '${t.localizedMessage}' "
            }
            logger.error { KOTLIN_VERSION }
            logger.error { stopWatcher.stringExecutionTime }
            logger.error { "$applicationName exception \n ${t.javaClass.canonicalName}: '${t.localizedMessage}'" }
        }

        override fun onComplete() {
            logger.info { memoryStatistics }
            logger.info { "finally initialization '$userApplicationName' ($applicationName)" }
            logger.info { stopWatcher.stringExecutionTime }
        }

        @Throws(Exception::class)
        override fun destroy() {
            logger.info { memoryStatistics }
            val applicationName = this.javaClass.canonicalName
            logger.info { "█($applicationName, $userApplicationName)" }
            logger.info { STOPPED }
            logger.info { KOTLIN_VERSION }
            logger.info { "$applicationName: ${stopWatcher.stringExecutionTime}" }
        }
    }

    private val className = this.javaClass.simpleName

    @Autowired
    fun initializeGenericContext(
        applicationContext: ApplicationContext,
        genericApplicationContext: GenericApplicationContext,
        buildProperties: BuildProperties,
        @Value("\${$JUNIT_MODE:false}")
        junitMode: Boolean,
        @Value("\${$SPRING_ACTIVE_PROFILE:dev-mode}")
        profileName: String,
    ) {
        Companion.applicationContext = applicationContext
        Companion.genericApplicationContext = genericApplicationContext
        initializeSpringBootApplication()
        logger.info {
            "Application '${className.clearName()}' is activated " +
                    "[${applicationContext.beanDefinitionCount} spring beans in pool]"
        }

        if (junitMode) {
            logger.info { "activate ReactorDebugAgent ..." }
            ReactorDebugAgent.init()
//            BlockHound.install()
        }

        if (profileName.contains("dev")) {

            val configBeansList = applicationContext.beanDefinitionNames
                .asSequence()
                .map { it.clearName() }
                .filter {
                    !it.contains("$")
                            && !it.contains("#")
                            && it.contains(".")
                            && it.let {
                        it.contains("springframework")
                                && !it.contains("internal")
                    }
                }.sorted()
                .toList()

            logger.warn { "There are ${configBeansList.size} autoconfigure spring beans in pool" }
            configBeansList.forEach { logger.trace { it } }
        }

        val strDetails = "### ${buildProperties.name}: application build time is ${buildProperties.time.toString2()} "

        logger.info { BANNER_ROW_BOLD_DELIMITER }
        logger.info { strDetails.plus("#".repeat(BANNER_ROW_BOLD_DELIMITER.length - strDetails.length)) }
        logger.info { BANNER_ROW_BOLD_DELIMITER }

        ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean::class.java).apply {
            logger.info { WmBuilder(VMS).buildLog() }
            logger.info { BANNER_ROW_BOLD_DELIMITER }
        }

        // SysEnv
        appCreateTime.update(buildProperties.time)
        userApplicationName.update(buildProperties.name)
    }

    @Bean
    open fun exitCodeGenerator(): ExitCodeGenerator = ExitCodeGenerator { MN42 }

    //==========================================================================
    private fun initializeSpringBootApplication() {
        //empty (4 override)
    }

    //==========================================================================
    @Bean
    open fun validateApplication(genericApplicationContext: GenericApplicationContext): CommandLineRunner =
        CommandLineRunner { args: Array<String> -> this.run(args) }

    companion object : Logging {

        val userApplicationName by lazy { LateInitVal<String>() }
        var isRunning = false
        var serverPort = 0

        //==========================================================================
        lateinit var applicationContext: ApplicationContext
        lateinit var genericApplicationContext: GenericApplicationContext

        //==========================================================================
        private val EMPTY_INITIALIZATION = SpringBootInititializer {}
        private val EMPTY_BANNER_INITIALIZATION = BannerInititializer { createMap() }

        //==========================================================================
        fun <T : Any> runSpringBootApplication(args: Array<String>, springBootClass: KClass<T>) =
            runSpringBootApplication(args, springBootClass, EMPTY_INITIALIZATION)

        fun <T : Any> runSpringBootApplication(
            args: Array<String>,
            springBootClass: KClass<T>,
            bi: BannerInititializer
        ) =
            runSpringBootApplication(args, springBootClass, EMPTY_INITIALIZATION, bi)

        private fun <T : Any> runSpringBootApplication(
            args: Array<String>,
            springBootClass: KClass<T>,
            sbi: SpringBootInititializer = EMPTY_INITIALIZATION,
            bi: BannerInititializer = EMPTY_BANNER_INITIALIZATION
        ) = springBootClass.run {

            if (args.isNotEmpty()) {
                StringBuilder(SB_DEF_CAPACITY * args.size).also { sb ->
                    args.iterator().forEach { sb.append("$it ") }
                    logger.debug { "args = $sb}" }
                }
            }

            (getProperty(USER_TIME_ZONE)
                ?.also { logger.info { "assigned time zone: '$it'" } }) ?: run {
                setProperty(USER_TIME_ZONE, defaultZoneId)
                TimeZone.setDefault(TimeZone.getTimeZone(defaultZoneId))
                    .also { logger.warn { "timezone is not assigned ($USER_TIME_ZONE), set defaultTimeZone: $defaultZoneId" } }
            }

            setProperty(SPRING_DEVTOOLS_RESTART_ENABLED, "false")
            //setProperty("LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_BOOT", "DEBUG");

            val applicationName = qualifiedName ?: error("qualifiedName is null")

            logger.info { "starting '$applicationName' ..." }

            toMono().subscribe(SpringBootSubscriber(applicationName, args, sbi, bi))

            if (!isRunning) {
                logger.info { "Shutdown APPLICATION $applicationName" }
                sleep(APP_SHUTDOWN_DELAY)
                //SpringApplication.exit(applicationContext);
                exitProcess(DEF_EXIT_PROCESS)
            }
        }
    }
}
