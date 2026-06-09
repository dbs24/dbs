package org.dbs.spring.boot.api

import com.sun.management.HotSpotDiagnosticMXBean
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.nullsafe.StopWatcher.Companion.defaultZoneId
import org.dbs.application.core.service.funcs.GetNetworkAddress
import org.dbs.application.core.service.funcs.GetNetworkAddress.currentHostName
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toString2
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.application.core.service.funcs.StringFuncs.clearName
import org.dbs.application.core.service.funcs.SysEnvFuncs.appCreateTime
import org.dbs.application.core.service.funcs.SysEnvFuncs.memoryStatistics
import org.dbs.application.core.service.funcs.SysEnvFuncs.processHandleInfo
import org.dbs.consts.SpringCoreConst.App.BUFFER_APP_SIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.JUNIT_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_DEVTOOLS_RESTART_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.USER_TIME_ZONE
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.dbs.consts.SysConst.DEF_EXIT_PROCESS
import org.dbs.consts.SysConst.HotSpotConsts.VMS
import org.dbs.consts.SysConst.MN42
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.spring.boot.banner.BannerBuilder
import org.dbs.spring.boot.banner.BannerInititializer
import org.dbs.spring.boot.vm.WmBuilder
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.support.GenericApplicationContext
import reactor.tools.agent.ReactorDebugAgent
import java.lang.management.ManagementFactory
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass
import kotlin.system.exitProcess

@ConfigurationPropertiesScan(basePackages = [ALL_PACKAGES])
abstract class AbstractSpringBootApplication : Logging {

    @Value("\${config.security.secured-params:password,username}")
    private val securedParams: String = "password,username"

    private fun run(args: Array<String>) {
        val applicationName = this.javaClass.canonicalName
        logger.info { "Application is validated '${applicationName.clearName()}'" }
    }

    @Autowired
    fun initializeGenericContext(
        applicationContext: ApplicationContext,
        genericApplicationContext: GenericApplicationContext,
        buildProperties: BuildProperties,
        @Value("\${$JUNIT_MODE:false}") junitMode: Boolean,
    ) {
        contextRef.set(applicationContext)
        genericContextRef.set(genericApplicationContext)
        initializeSpringBootApplication()

        if (junitMode) {
            logger.info { "activate ReactorDebugAgent ..." }
            ReactorDebugAgent.init()
        }

        logger.info { "### ${buildProperties.name}: application build time is ${buildProperties.time.toString2()}" }

        runCatching {
            ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean::class.java).apply {
                logger.info { WmBuilder(VMS).buildLog() }
            }
        }

        appCreateTime.updateOnce(buildProperties.time)
        userApplicationName.updateOnce(buildProperties.name)
    }

    @Bean
    open fun exitCodeGenerator(): ExitCodeGenerator = ExitCodeGenerator { MN42 }

    protected open fun initializeSpringBootApplication() {
        // Доступно для переопределения в наследниках
    }

    @Bean
    open fun validateApplication(genericApplicationContext: GenericApplicationContext): CommandLineRunner =
        CommandLineRunner { args: Array<String> -> this.run(args) }

    companion object : Logging {
        val userApplicationName by lazy { LateInitVal<String>() }

        @Volatile var isRunning = false
        @Volatile var serverPort = 0

        private val contextRef = AtomicReference<ApplicationContext?>()
        private val genericContextRef = AtomicReference<GenericApplicationContext?>()

        var applicationContext: ApplicationContext
            get() = contextRef.get() ?: error("ApplicationContext is not initialized yet")
            set(value) = contextRef.set(value)

        var genericApplicationContext: GenericApplicationContext
            get() = genericContextRef.get() ?: error("GenericApplicationContext is not initialized yet")
            set(value) = genericContextRef.set(value)

        private val EMPTY_INITIALIZATION = SpringBootInititializer {}
        private val EMPTY_BANNER_INITIALIZATION = BannerInititializer { createMap() }

        // Класс полностью автономен, не удерживает внешних ссылок и безопасно очищает ресурсы
        private class SpringBootLifecycleHandler<T : Any>(
            private val applicationName: String,
            private val args: Array<String>,
            private val sbi: SpringBootInititializer,
            private val bi: BannerInititializer,
        ) : Logging, DisposableBean {

            // Храним сообщение для вывода при завершении работы приложения
            @Volatile private var shutdownMessage: String = UNKNOWN

            fun start(clazz: KClass<T>) {
                logger.debug { "Initializing Spring Boot application ${clazz.simpleName}" }
                BannerBuilder(bi.initialize()).build(clazz)

                try {
                    val app = SpringApplication(clazz.java).apply {
                        applicationStartup = BufferingApplicationStartup(BUFFER_APP_SIZE)
                        applicationStartup.start(clazz.simpleName ?: UNKNOWN)

                        // Идиоматичная передача базовых свойств Spring Boot 3.4 без глобального системного стейта
                        setDefaultProperties(mapOf(SPRING_DEVTOOLS_RESTART_ENABLED to "false"))
                    }

                    val context = app.run(*args)
                    sbi.initialize()

                    if (context is ReactiveWebServerApplicationContext) {
                        serverPort = context.webServer.port
                    }

                    logger.debug { "${GetNetworkAddress.allAddresses}, port $serverPort" }
                    isRunning = true

                    shutdownMessage = "$applicationName, $currentHostName, ${GetNetworkAddress.allAddresses}, port = $serverPort"

                    if (context is GenericApplicationContext) {
                        context.displayName = clazz.simpleName ?: clazz.toString()
                    }

                    logger.info { "finally initialization '${userApplicationName.value}' ($applicationName)" }

                } catch (t: Throwable) {
                    val devToolsExceptionClass = "org.springframework.boot.devtools.restart.SilentExitExceptionHandler\$SilentExitException"

                    val isSilentExit = t.javaClass.name == devToolsExceptionClass
                            || t.cause?.javaClass?.name == devToolsExceptionClass

                    if (isSilentExit) {
                        logger.debug { "Spring Boot DevTools triggered a silent thread exit for restart." }
                        return // SWALLOW AND EXIT CLEANLY: Do not rethrow, do not exitProcess.
                    }

                    // Real structural application errors remain here
                    logger.error(t) {
                        "CRITICAL ERROR DURING INITIALIZATION '$applicationName', reason: '${t.localizedMessage}'"
                    }
                    exitProcess(DEF_EXIT_PROCESS)
                }
            }

            // Автоматический хук Spring очистки ресурсов. Исключает утечку Shutdown Hooks при перезапуске контекстов.
            override fun destroy() {
                logger.info { shutdownMessage }
                logger.info { processHandleInfo }
                logger.info { "$applicationName: server shut down" }
                logger.info { memoryStatistics }

                val currentAppName = this.javaClass.canonicalName
                logger.info { "█($currentAppName, ${userApplicationName.valueOrNull})" }
            }
        }

        fun <T : Any> runSpringBootApplication(
            args: Array<String>,
            springBootClass: KClass<T>,
            bi: BannerInititializer
        ) = runSpringBootApplication(args, springBootClass, EMPTY_INITIALIZATION, bi)

        fun <T : Any> runSpringBootApplication(
            args: Array<String>,
            springBootClass: KClass<T>,
            sbi: SpringBootInititializer = EMPTY_INITIALIZATION,
            bi: BannerInititializer = EMPTY_BANNER_INITIALIZATION
        ) {
            if (args.isNotEmpty()) {
                logger.debug { "args = ${args.joinToString(separator = " ")}" }
            }

            // FIX A: System properties MUST be set here BEFORE SpringApplication is instantiated
            System.setProperty("spring.devtools.restart.enabled", "false")

            val zoneId = System.getProperty(USER_TIME_ZONE) ?: defaultZoneId
            System.setProperty(USER_TIME_ZONE, zoneId)
            TimeZone.setDefault(TimeZone.getTimeZone(zoneId))
            logger.info { "assigned time zone: '$zoneId'" }

            val applicationName = springBootClass.qualifiedName ?: error("qualifiedName is null")
            logger.info { "starting '$applicationName' ..." }

            val lifecycleHandler = SpringBootLifecycleHandler<T>(applicationName, args, sbi, bi)
            lifecycleHandler.start(springBootClass)
        }
    }
}
