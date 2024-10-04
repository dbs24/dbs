package org.dbs.sandbox.config

import org.dbs.config.MainApplicationConfig
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
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.metrics.JvmMetricsAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.metrics.LogbackMetricsAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.observation.web.client.HttpClientObservationsAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.r2dbc.ConnectionFactoryHealthContributorAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.session.SessionsEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.system.DiskSpaceHealthContributorAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.web.exchanges.HttpExchangesEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.web.reactive.ReactiveManagementContextAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
import org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.ReactiveMultipartAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
import org.springframework.context.annotation.Configuration


@EnableAutoConfiguration(exclude = [
    DataSourceAutoConfiguration::class,
    JvmMetricsAutoConfiguration::class,
    LogbackMetricsAutoConfiguration::class,
    MetricsAutoConfiguration::class,
    CodecsAutoConfiguration::class,
    ReactiveMultipartAutoConfiguration::class,
    CompositeMeterRegistryAutoConfiguration::class,
    SessionAutoConfiguration::class,
    SecurityAutoConfiguration::class,
    ManagementContextAutoConfiguration::class,
    EmbeddedWebServerFactoryCustomizerAutoConfiguration::class,
    TransactionAutoConfiguration::class,
    SqlInitializationAutoConfiguration::class,
    GroovyTemplateAutoConfiguration::class,
    RedisRepositoriesAutoConfiguration::class,
    R2dbcDataAutoConfiguration::class,
    DataSourceTransactionManagerAutoConfiguration::class,
    LifecycleAutoConfiguration::class,
    ConfigurationPropertiesAutoConfiguration::class,
    AopAutoConfiguration::class,
    ReactiveManagementContextAutoConfiguration::class,
    HttpExchangesEndpointAutoConfiguration::class,
    SessionsEndpointAutoConfiguration::class,
    ReactiveOAuth2ResourceServerAutoConfiguration::class,
    ConnectionFactoryHealthContributorAutoConfiguration::class,
    HttpClientObservationsAutoConfiguration::class,
    WebClientAutoConfiguration::class,
    ClientHttpConnectorAutoConfiguration::class,
    GsonAutoConfiguration::class,
    ThreadDumpEndpointAutoConfiguration::class,
    HeapDumpWebEndpointAutoConfiguration::class,
    LoggersEndpointAutoConfiguration::class,
    LogFileWebEndpointAutoConfiguration::class,
    DiskSpaceHealthContributorAutoConfiguration::class,
    EnvironmentEndpointAutoConfiguration::class,
    JacksonEndpointAutoConfiguration::class,
    ConfigurationPropertiesReportEndpointAutoConfiguration::class,
    ShutdownEndpointAutoConfiguration::class,
    ConditionsReportEndpointAutoConfiguration::class,
    AuditEventsEndpointAutoConfiguration::class,
])
@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
class SandBoxConfig : MainApplicationConfig()
