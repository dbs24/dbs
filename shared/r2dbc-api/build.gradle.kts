import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.SECURITY_MANAGER_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(SPRING_CONFIG))
    api(project(SECURITY_MANAGER_API))

    implementation(libs.spring.data.r2dbc)
    implementation(libs.spring.data.redis)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.r2dbc.postgresql)
    implementation(libs.r2dbc.pool)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.kotlin.stdlib.jdk8)

    compileOnly(libs.jmh.core)
    compileOnly(libs.jmh.annotations)
}

description = "r2dbc-api"
