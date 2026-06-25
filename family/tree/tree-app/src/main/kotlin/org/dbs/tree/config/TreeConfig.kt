package org.dbs.tree.config

import org.dbs.config.AbstractWebSecurityConfig
import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.springframework.boot.actuate.autoconfigure.audit.AuditEventsEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.condition.ConditionsReportEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.context.ShutdownEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.context.properties.ConfigurationPropertiesReportEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.endpoint.jackson.JacksonEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.env.EnvironmentEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.logging.LogFileWebEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.logging.LoggersEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.management.HeapDumpWebEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.management.ThreadDumpEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.web.exchanges.HttpExchangesEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
import org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy


@EnableAutoConfiguration(exclude = [
    ManagementContextAutoConfiguration::class,
    LifecycleAutoConfiguration::class,
    ConfigurationPropertiesAutoConfiguration::class,
    AopAutoConfiguration::class,
    HttpExchangesEndpointAutoConfiguration::class,
    ThreadDumpEndpointAutoConfiguration::class,
    HeapDumpWebEndpointAutoConfiguration::class,
    LoggersEndpointAutoConfiguration::class,
    LogFileWebEndpointAutoConfiguration::class,
    EnvironmentEndpointAutoConfiguration::class,
    JacksonEndpointAutoConfiguration::class,
    ConfigurationPropertiesReportEndpointAutoConfiguration::class,
    ShutdownEndpointAutoConfiguration::class,
    ConditionsReportEndpointAutoConfiguration::class,
    AuditEventsEndpointAutoConfiguration::class,
])
@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true)
class TreeConfig : AbstractWebSecurityConfig()
