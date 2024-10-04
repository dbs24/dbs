package org.dbs.config

import com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.dbs.application.core.service.funcs.StringFuncs.clearName
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan


@ComponentScan(basePackages = [ALL_PACKAGES])
abstract class MainApplicationConfig : AbstractApplicationConfiguration() {
    @Bean
    open fun objectMapper(): ObjectMapper = jacksonObjectMapper().run {
        logger.trace { "Initialize ObjectMapper (${this.javaClass.canonicalName})" }
        enable(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        // игнорируем ненужные поля
        configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        //objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    //==========================================================================
    override fun initialize() = super.initialize().also {
        logger.trace { "Configuration '${this.javaClass.simpleName.clearName()}' is activated" }
    }
}
