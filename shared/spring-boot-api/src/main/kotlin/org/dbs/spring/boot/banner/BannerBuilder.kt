package org.dbs.spring.boot.banner

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.SysEnvFuncs.findResourceFile
import org.dbs.consts.SpringCoreConst.DELIMITER
import org.dbs.consts.SpringCoreConst.PropertiesNames.BANNER_ROW_DELIMITER
import org.dbs.consts.SpringCoreConst.PropertiesNames.CUSTOM_BANNER_SET_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.LOGGING_LEVEL_ROOT
import org.dbs.consts.SpringCoreConst.PropertiesNames.SD_API_DOCS_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SD_SWAGGER_UI_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SESSION_TIME_OUT
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_ACTIVE_PROFILE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_APPLICATION_NAME
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_BOOT_LAZY_INIT
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_BOOT_VERSION
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_BOOT_WEB_APP_TYPE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_THREADS_VIRTUAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_WEB_LOCALE
import org.dbs.consts.StringMap
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.RESOURCE_FLD
import org.dbs.consts.SysConst.SB_ROW_CAPACITY
import java.io.File
import java.nio.charset.Charset.defaultCharset
import kotlin.reflect.KClass

@JvmInline
value class BannerBuilder(private val properties: StringMap) : Logging {

    val defaultLogo: String
        get() = """            
            Default Banner
    """.trimIndent()


    fun <T : Any> build(clazz: KClass<T>) {
        if (!properties.keys.contains(CUSTOM_BANNER_SET_KEY)) {
            processBannerUpdate(clazz)
        }
    }

    private fun readLogo(appNameFromPackage: String) = run {
        val bannerHolder by lazy { LateInitVal<String>() }
        findResourceFile("logo.txt", appNameFromPackage) {
            bannerHolder.init("\n" + File(it).readText(defaultCharset()) + "\n")
        }
        bannerHolder.valueOrDefault(defaultLogo)
    }

    private fun <T : Any> processBannerUpdate(clazz: KClass<T>) {
        val fld = clazz.java.protectionDomain.codeSource.location.path
        val fldPos = fld.indexOf("build/classes/kotlin/main")

        if (fldPos > 0) {

            val delimiter = "$BANNER_ROW_DELIMITER\n"
            val commonProperties = mapOf(
                "SpringBoot Version" to SPRING_BOOT_VERSION,
                "Application name" to SPRING_APPLICATION_NAME,
                "SpringBoot active profile" to SPRING_ACTIVE_PROFILE,
                "Web Application Type" to SPRING_BOOT_WEB_APP_TYPE,
                "Lazy initialization" to SPRING_BOOT_LAZY_INIT,
                //"Virtual Threads" to SPRING_THREADS_VIRTUAL,
                DELIMITER to EMPTY_STRING,
                "Logging level root" to LOGGING_LEVEL_ROOT,
                "Default locale " to SPRING_WEB_LOCALE,
                DELIMITER to EMPTY_STRING,
                "Session timeout" to SERVER_SESSION_TIME_OUT,
                DELIMITER to EMPTY_STRING,
                "SpringDoc API" to SD_API_DOCS_ENABLED,
                "SpringDoc Swagger UI" to SD_SWAGGER_UI_ENABLED,
                DELIMITER to EMPTY_STRING,
            )
            val writeContent = delimiter.plus(
                (commonProperties + properties).let { unionMap ->
                    val maxKeyLength = unionMap.keys
                        .filter { !it.contains(BANNER_ROW_DELIMITER) }
                        .maxOfOrNull { it.length }
                    val propertyMask = "\${%s}"
                    StringBuilder(SB_ROW_CAPACITY * unionMap.size).also { sb ->
                        unionMap.asSequence().forEach { mapItem ->
                            if (mapItem.key.contains(BANNER_ROW_DELIMITER))
                                sb.append(delimiter) else {
                                sb.append(
                                    String.format(
                                        "%-${maxKeyLength}s : $propertyMask\n",
                                        mapItem.key.let {
                                            it.substring(0, 1).uppercase().plus(it.substring(1))
                                        }, mapItem.value
                                    )
                                )
                            }
                        }
                    }.toString()
                }
            )

            val resourcesFolder = fld.substring(0, fldPos).plus(RESOURCE_FLD)
            val srcFolder = File(resourcesFolder)
            if (srcFolder.isDirectory) {
                val bannerFile = resourcesFolder.plus("/banner.txt")
                File(bannerFile).bufferedWriter()
                    .use { it.write(readLogo(fld.substring(0, fldPos - 1)) + writeContent) }
                logger.debug { "create/update banner '$bannerFile'" }
            } else {
                logger.error { "resource '$resourcesFolder' doesn't exist, can't create banner" }
            }
        }
    }
}
