package org.dbs.mail.config

import org.apache.logging.log4j.kotlin.logger
import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.safe.school.mail.consts.NotificationConsts.MAIL_TEMPLATE_PREFIX
import org.dbs.safe.school.mail.consts.NotificationConsts.MAIL_TEMPLATE_SUFFIX
import org.dbs.safe.school.mail.consts.NotificationConsts.MESSAGE_SOURCE_BASE_NAME
import org.dbs.service.I18NService
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
import org.springframework.boot.health.autoconfigure.application.DiskSpaceHealthContributorAutoConfiguration
import org.springframework.boot.http.codec.autoconfigure.CodecsAutoConfiguration
import org.springframework.boot.micrometer.metrics.autoconfigure.CompositeMeterRegistryAutoConfiguration
import org.springframework.boot.micrometer.metrics.autoconfigure.MetricsAutoConfiguration
import org.springframework.boot.micrometer.metrics.autoconfigure.jvm.JvmMetricsAutoConfiguration
import org.springframework.boot.micrometer.metrics.autoconfigure.logging.logback.LogbackMetricsAutoConfiguration
import org.springframework.boot.reactor.netty.autoconfigure.actuate.web.server.NettyReactiveManagementContextAutoConfiguration
import org.springframework.boot.webflux.autoconfigure.ReactiveMultipartAutoConfiguration
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.support.ResourceBundleMessageSource
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode.HTML
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver
import java.nio.charset.StandardCharsets.UTF_8

@EnableAutoConfiguration(exclude = [
    JvmMetricsAutoConfiguration::class,
    LogbackMetricsAutoConfiguration::class,
    MetricsAutoConfiguration::class,
    CodecsAutoConfiguration::class,
    ReactiveMultipartAutoConfiguration::class,
    CompositeMeterRegistryAutoConfiguration::class,
    ManagementContextAutoConfiguration::class,
    LifecycleAutoConfiguration::class,
    ConfigurationPropertiesAutoConfiguration::class,
    AopAutoConfiguration::class,
    NettyReactiveManagementContextAutoConfiguration::class,
    HttpExchangesEndpointAutoConfiguration::class,
    DiskSpaceHealthContributorAutoConfiguration::class,
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
class MailConfig {

    @Primary
    @Bean
    fun thymeleafTemplateResolver(): ITemplateResolver = ClassLoaderTemplateResolver().apply {
        prefix = MAIL_TEMPLATE_PREFIX
        suffix = MAIL_TEMPLATE_SUFFIX
        templateMode = HTML
        characterEncoding = UTF_8.name()
    }

    @Bean
    fun thymeleafTemplateEngine(resolver: ITemplateResolver, messageSource: ResourceBundleMessageSource)
            : SpringTemplateEngine = SpringTemplateEngine().apply {
        setTemplateResolver(resolver)
        setTemplateEngineMessageSource(messageSource)
    }

    @Bean
    fun messageSource(i18NService: I18NService): MessageSource = ResourceBundleMessageSource().apply {
        logger.trace("init i18n: $i18NService")
        setBasename(MESSAGE_SOURCE_BASE_NAME)
        setDefaultEncoding(UTF_8.name())
        setDefaultLocale(I18NService.systemLocale)
    }
}
