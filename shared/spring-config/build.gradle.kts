import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {

    api(project(SPRING_CORE_API))
    api(project(APPLICATION_CORE_API))

    implementation(libs.spring.webflux)
    implementation(libs.spring.json)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)
}

description = "spring-config"
