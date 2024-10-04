package org.dbs.service

import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_URL
import org.dbs.spring.core.api.PublicApplicationBean
import io.r2dbc.spi.ConnectionFactory
import org.dbs.application.core.service.funcs.CollectionProcessor
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.SysConst.EMPTY_STRING
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement


@EnableTransactionManagement
sealed class GenericR2dbcConfiguration : PublicApplicationBean, AbstractR2dbcConfiguration() {

    @Value("\${$SPRING_R2DBC_URL}")
    val url = EMPTY_STRING

//    @Bean
//    fun auditorAware(): ReactiveAuditorAware<String> {
//        return ReactiveAuditorAware { Mono.just(javaClass.canonicalName) }
//    }

    @Bean
    fun r2dbcTransactionManager(connectionFactory: ConnectionFactory) =
        R2dbcTransactionManager(connectionFactory.also {
            logger.debug { "initialize connectionFactory: $connectionFactory" }
        }).also {
            logger.debug { "initialize r2dbcTransactionManager: ${it.javaClass.canonicalName}" }
        }

    override fun getCustomConverters(): List<Any> = createCollection(CollectionProcessor {
        //-----------------------------------------------
        addExtraCustomConverters(it)
    }).also { logger.debug { "registry ${it.size} custom converter(s)" } } as List<Any>

    protected fun addExtraCustomConverters(converters: MutableCollection<Any>) {
        logger.debug { "addExtraCustomConverters" }
    }

    override fun initialize() {
        logger.debug { "$url, (${this.javaClass.canonicalName})) " }
    }

    override fun shutdown() {
        logger.debug { "shutdown GenericR2dbcConfiguration" }
    }

}
