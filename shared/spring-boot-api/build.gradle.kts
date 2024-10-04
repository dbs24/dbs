import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    implementation(libs.spring.webflux)
    implementation(libs.reactor.tools)
    implementation(libs.spring.security)
    implementation(libs.spring.security.jwt)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.log4j.core)
    runtimeOnly(libs.log4j.jakarta.smtp)

    testApi(libs.spring.starter.test)
    implementation(libs.kotlin.stdlib.jdk8)

}
description = "spring-boot-api"
