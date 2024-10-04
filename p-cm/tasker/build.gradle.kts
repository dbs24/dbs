import dsl.Dependencies.ApplicationAttributes.MAIN_CLASS
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_TITLE
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_VERSION
import dsl.Dependencies.ProjectAttributes.MAIN_CLASS_NAME
import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.CACHE_STARTER
import dsl.Dependencies.Projects.CM_ANALYST_API
import dsl.Dependencies.Projects.CM_TASKER
import dsl.Dependencies.Projects.KAFKA_API
import dsl.Dependencies.Projects.NO_VERSION_ASSIGNED
import dsl.Dependencies.Projects.SECURITY_CONFIG_STARTER
import dsl.Dependencies.Projects.SECURITY_MANAGER_API
import dsl.Dependencies.Projects.SPRING_BOOT_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_CORE_API
import dsl.Dependencies.Projects.SPRING_KAFKA_STARTER
import dsl.Dependencies.Projects.TEST_CORE

plugins {
    application
    idea
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.gitlab.arturbosch.detekt")
    //id("org.graalvm.buildtools.native")
}

val springBootGroup = "org.springframework.boot"
val springDocGroup = "org.springdoc"
val googleGroup = "com.google.code.gson"
// exludes
val springExcStarter = "spring-boot-starter"
val springExcStarterLogging = "spring-boot-starter-logging"
val mainApplicationClassName = "org.dbs.tasker.TaskerApplication"

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(CACHE_STARTER))
    api(project(CM_ANALYST_API))
    api(project(KAFKA_API))
    api(project(SECURITY_CONFIG_STARTER))
    api(project(SECURITY_MANAGER_API))
    api(project(SPRING_BOOT_API))
    api(project(SPRING_CONFIG))
    api(project(SPRING_CORE_API))
    api(project(SPRING_KAFKA_STARTER))
    testApi(project(TEST_CORE))

    implementation(libs.commons.collections4)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)
    implementation(libs.coroutines.reactor)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jboss.logging)
    implementation(libs.jboss.marshalling)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.script.runtime)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.log4j.jcl)
    implementation(libs.spring.webflux)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.spring.autoconfigure)
    implementation(libs.spring.configuration.processor)
    implementation(libs.spring.json)
    implementation(libs.spring.kafka)
    implementation(libs.spring.security)
    implementation(libs.spring.starter)
    implementation(libs.neatchess)
    implementation(platform(libs.kotlin.bom))
    implementation(platform(libs.log4j.bom))
    implementation(platform(libs.spring.dependencies))

    testApi(libs.coroutines.test)
    testApi(libs.kotest.assertions.core)
    testApi(libs.kotest.extensions.spring)
    testApi(libs.kotest.property.jvm)
    testApi(libs.kotest.runner.junit5.jvm)
    testApi(libs.kotest.testcontainers)
    testApi(libs.lincheck)
    testApi(libs.reactor.test)
    testApi(libs.spring.starter.test)
    testApi(libs.testcontainers.junit.jupiter)
    testApi(platform(libs.kotest.bom))

    compileOnly(libs.kotlin.noarg)
    compileOnly(libs.kotlin.osgi.bundle)
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.spring.autoconfigure.processor)

    annotationProcessor(libs.spring.configuration.processor)
    annotationProcessor(libs.spring.autoconfigure.processor)

    configurations {
        all {
            exclude(group = springBootGroup, module = "spring-boot-starter-logging")
            exclude(group = springBootGroup, module = "junit-vintage-engine")
            exclude(group = springDocGroup, module = "jackson-core")
        }
    }
}

springBoot {
    buildInfo()
    mainClass = mainApplicationClassName
}

tasks.jar {
    manifest {
        attributes[MAIN_CLASS] = mainApplicationClassName
        attributes[IMPLEMENTATION_TITLE] = project.name
        attributes[IMPLEMENTATION_VERSION] = project.version
    }
    project.version = NO_VERSION_ASSIGNED
    //project.setProperty(MAIN_CLASS_NAME, mainApplicationClassName)
    archiveBaseName.set(project.name)
}

application {
    mainClass.set(mainApplicationClassName)
}

//graalvmNative {
//    binaries {
//        named("main") {
//            javaLauncher.set(javaToolchains.launcherFor {
//                languageVersion.set(JavaLanguageVersion.of(17))
//                vendor.set(JvmVendorSpec.matching("GraalVM Community"))
//            })
//            mainClass.set(mainApplicationClassName)
//        }
//    }
//}

description = CM_TASKER

tasks.test {
    useJUnitPlatform()
}
