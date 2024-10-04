import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.ENTITY_CORE_API
import dsl.Dependencies.Projects.KAFKA_API
import dsl.Dependencies.Projects.PERSISTENCE_API
import dsl.Dependencies.Projects.R2DBC_API
import dsl.Dependencies.Projects.REF_STARTER
import dsl.Dependencies.Projects.SECURITY_MANAGER_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(ENTITY_CORE_API))
    api(project(SPRING_CONFIG))
    api(project(SECURITY_MANAGER_API))
    api(project(PERSISTENCE_API))
    api(project(R2DBC_API))
    api(project(KAFKA_API))
    api(project(REF_STARTER))

    implementation(libs.jjwt.api)
    implementation(libs.spring.webflux)
    implementation(libs.reactor.netty)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)
    implementation(libs.coroutines.reactor)

    compileOnly(libs.spring.data.r2dbc)
    compileOnly(libs.bucket4j.core)
    compileOnly(libs.jmh.core)
    compileOnly(libs.jmh.annotations)
    testApi(libs.spring.starter.test)
}

description = "spring-rest-api"
