import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.SPRING_CORE_API
import dsl.Dependencies.Projects.SPRING_REST_API
import dsl.Dependencies.Projects.TEST_API
import dsl.Dependencies.Projects.TEST_JUNIT_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(TEST_API))
    api(project(TEST_JUNIT_API))
    api(project(SPRING_CORE_API))
    api(project(SPRING_REST_API))

    implementation(platform(libs.kotest.bom))
    implementation(libs.testcontainers)
    implementation(libs.testcontainers.junit.jupiter)
    implementation(libs.spring.json)
    implementation(libs.testcontainers.postgresql)
    implementation(libs.testcontainers.kafka)
    implementation(libs.testcontainers.r2dbc)
    implementation(libs.spring.starter.test)
    implementation(libs.spring.starter.test.autoconfigure)
    implementation(libs.spring.webflux)
    implementation(libs.kotlin.test.junit)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.kotest.blockhound)
    testApi(libs.blockhound.junit.platform)

    implementation(libs.kotest.assertions.core)
    implementation(libs.kotest.runner.junit5.jvm)
    implementation(libs.kotest.property.jvm)
    implementation(libs.kotest.extensions.spring)
    implementation(libs.kotest.testcontainers)



//    configurations.implementation {
//        exclude(group = "org.testcontainers", module = "jackson-annotations")
//        exclude(group = "org.testcontainers", module = "slf4j-api")
//    }

    implementation(libs.kotlin.stdlib.jdk8)

}

description = "test-core"
