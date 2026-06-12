package org.dbs.spring.boot.env

import org.apache.logging.log4j.kotlin.Logging
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.util.*

class EnvFileEnvironmentPostProcessor : EnvironmentPostProcessor, Ordered {

    override fun getOrder() = ORDER

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        // Filesystem .env overrides classpath .env but is still a low-priority fallback
        val fsEnvFile = File(ENV_FILE_NAME)
        if (fsEnvFile.exists()) {
            loadProperties(fsEnvFile.readText(), ENV_SOURCE_FS)
                ?.also { environment.propertySources.addLast(it) }
        }

        // Classpath .env provides defaults — lowest priority
        val classpathEnvFile = ClassPathResource(ENV_FILE_NAME)
        if (classpathEnvFile.exists()) {
            loadProperties(classpathEnvFile.inputStream.bufferedReader().readText(), ENV_SOURCE_CLASSPATH)
                ?.also { environment.propertySources.addLast(it) }
        }
    }

    private fun loadProperties(content: String, sourceName: String): PropertiesPropertySource? {
        val properties = Properties()
        content.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") && it.contains("=") }
            .forEach { line ->
                val idx = line.indexOf('=')
                properties.setProperty(line.substring(0, idx).trim(), line.substring(idx + 1).trim())
            }
        return if (properties.isNotEmpty()) PropertiesPropertySource(sourceName, properties) else null
    }

    companion object : Logging {
        const val ENV_FILE_NAME = ".env"
        const val ENV_SOURCE_CLASSPATH = "dotEnvClasspath"
        const val ENV_SOURCE_FS = "dotEnvFilesystem"
        const val ORDER = Ordered.LOWEST_PRECEDENCE - 100
    }
}
