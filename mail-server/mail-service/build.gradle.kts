import dsl.Dependencies.ApplicationAttributes.MAIN_CLASS
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_TITLE
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_VERSION
import dsl.Dependencies.ProjectAttributes.MAIN_CLASS_NAME
import dsl.Dependencies.Projects.KAFKA_API
import dsl.Dependencies.Projects.MAIL_STARTER
import dsl.Dependencies.Projects.NO_VERSION_ASSIGNED
import dsl.Dependencies.Projects.SPRING_BOOT_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_KAFKA_STARTER
import dsl.Dependencies.Projects.TEST_CORE

plugins {
    application
    idea
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
}


val springBootGroup = "org.springframework.boot"
val springDocGroup = "org.springdoc"

val mainApplicationClassName = "org.dbs.mail.MailServerApplication"

dependencies {
    api(project(MAIL_STARTER))
    api(project(SPRING_BOOT_API))
    api(project(SPRING_CONFIG))
    api(project(SPRING_KAFKA_STARTER))
    api(project(KAFKA_API))
    testApi(project(TEST_CORE))

    implementation(platform(libs.kotlin.bom))
    implementation(libs.reactor.netty)
    implementation(libs.spring.json)
    implementation(libs.spring.starter)
    implementation(libs.spring.kafka)
    implementation(libs.spring.webflux)
    implementation(libs.spring.actuator)
    implementation(libs.spring.autoconfigure)
    implementation(libs.spring.actuator)
    implementation(libs.spring.configuration.processor)

    implementation(libs.jboss.logging)
    implementation(libs.swagger.annotations)
    implementation(libs.springdoc.openapi.webflux.ui)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)
    implementation(libs.coroutines.reactor)
    implementation(libs.coroutines.reactive)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.script.runtime)
    implementation(libs.spring.mail)
    implementation(libs.spring.thymeleaf)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.bucket4j.core)

    testApi(platform(libs.kotest.bom))
    testApi(libs.spring.starter.test)
    testApi(libs.testcontainers.junit.jupiter)
    testApi(libs.testcontainers.postgresql)
    testApi(libs.postgresql)
    testApi(libs.coroutines.test)

    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.kotlin.noarg)
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

description = "mail-service"

tasks.test {
    useJUnitPlatform()
}
