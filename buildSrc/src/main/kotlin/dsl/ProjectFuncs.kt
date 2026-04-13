package dsl

import dsl.Dependencies.Core.EMPTY_STRING
import dsl.Dependencies.ProtoBuffSettings.GRPC
import dsl.Dependencies.ProtoBuffSettings.GRPC_JAVA_LIB
import dsl.Dependencies.ProtoBuffSettings.GRPC_JAVA_LIB_VAR
import dsl.Dependencies.ProtoBuffSettings.GRPC_KT
import dsl.Dependencies.ProtoBuffSettings.GRPC_KT_LIB
import dsl.Dependencies.ProtoBuffSettings.GRPC_KT_LIB_VAR
import dsl.Dependencies.ProtoBuffSettings.JDK8_JAR
import dsl.Dependencies.ProtoBuffSettings.PROTOC_LIB
import dsl.Dependencies.ProtoBuffSettings.PROTOC_LIB_VAR
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.configure


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
