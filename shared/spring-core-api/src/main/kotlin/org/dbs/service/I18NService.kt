package org.dbs.service

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.api.LateInitVal
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_WEB_LOCALE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_WEB_LOCALE_DEF_VAL
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.enums.I18NEnum
import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.ext.LocaleFuncs.isValidLocale
import org.dbs.ext.LocaleFuncs.locale
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.core.api.ApplicationBean.Companion.findCanonicalService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux.fromArray
import java.util.*

@Service
@Lazy(false)
class I18NService : AbstractApplicationService() {
    @Value("\${$SPRING_WEB_LOCALE:$SPRING_WEB_LOCALE_DEF_VAL}")
    fun initDefaultWebLocale(defaultWebLocale: String) {

        systemLocale.update(let {
            logger.debug { "defaultWebLocale: $defaultWebLocale" }

            require(defaultWebLocale.isValidLocale())
            { "$SPRING_WEB_LOCALE: Illegal web locale value - '$defaultWebLocale'" }

            defaultWebLocale.locale().also {
                Locale.setDefault(it)
            }
        }.also {
            logger.debug { "application system locale: $it" }
        })
    }

    companion object : Logging {

        private val messageSource by lazy { findCanonicalService(MessageSource::class) }
        val systemLocale by lazy { LateInitVal<Locale>() }

        fun getLocaleOrSystemLocale(locale: String?): Locale =
            locale?.let {
                if (locale.isValidLocale()) {
                    locale.locale()
                } else {
                    systemLocale.value
                }
            } ?: systemLocale.value

        fun findI18nMessage(i18nEnum: I18NEnum) =
            messageSource.getMessage(i18nEnum.name, null, i18nEnum.defaultMsgValue, systemLocale.value)
                ?: i18nEnum.defaultMsgValue

        fun findI18nMessage(i18nEnum: I18NEnum, vararg args: String) =
            messageSource.getMessage(i18nEnum.name, args, i18nEnum.defaultMsgValue, systemLocale.value)
                ?: i18nEnum.defaultMsgValue

        fun findI18nMessage(messageCode: String, vararg args: String) =
            messageSource.getMessage(messageCode, args, messageCode, systemLocale.value) ?: EMPTY_STRING

        init {
            fromArray(I18NEnum.entries.toTypedArray())
                .noDuplicates({ it.defaultMsgValue })
                .count()
                .map { logger.debug { "validate I18NEnum ($it records) " }; it }
                .subscribeMono()
        }
    }
}
