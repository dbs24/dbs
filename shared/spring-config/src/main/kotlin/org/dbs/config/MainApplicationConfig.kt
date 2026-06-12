package org.dbs.config

import org.dbs.application.core.service.funcs.StringFuncs.clearName
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import tools.jackson.databind.DefaultTyping
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import tools.jackson.module.kotlin.KotlinModule


@ComponentScan(basePackages = [ALL_PACKAGES])
abstract class MainApplicationConfig : AbstractApplicationConfiguration() {

    @Bean
    open fun objectMapper(): JsonMapper {
        val typeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType("org.dbs")
            .allowIfSubType("org.dbs")
            .allowIfSubType("java.util.")
            .build()

        return JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            // ✅ FIX: Drop the third parameter. Jackson 3 defaults to JsonTypeInfo.As.PROPERTY automatically.
            .activateDefaultTyping(typeValidator, DefaultTyping.NON_FINAL)
            .build()
    }

    //==========================================================================
    override fun initialize() = super.initialize().also {
        logger.trace { "Configuration '${this.javaClass.simpleName.clearName()}' is activated" }
    }
}
