package dsl

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension


object ProjectFuncs {

    fun Project.findArtifact(keyLibProperty: String, propertyName: String, extLib: String? = null): String {
        // Получаем расширение через базовый интерфейс ExtensionContainer
        val catalogs = extensions.findByName("versionCatalogs") as? VersionCatalogsExtension

        // Пытаемся найти каталог "libs"
        val libs: VersionCatalog? = catalogs?.find("libs")?.orElse(null)

        // Ищем версию в TOML, если не нашли — берем из свойств проекта
        val version = libs?.findVersion(propertyName)
            ?.map { it.requiredVersion }
            ?.orElseGet { this.findProperty(propertyName)?.toString() }
            ?: error("Version '$propertyName' not found in libs.version.toml")

        val extension = if (!extLib.isNullOrEmpty()) ":$extLib" else ""

        return "$keyLibProperty:$version$extension"
    }
}
