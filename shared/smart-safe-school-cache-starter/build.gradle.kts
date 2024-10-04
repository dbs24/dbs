import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.AUTH_SERVER_API
import dsl.Dependencies.Projects.CACHE_STARTER
import dsl.Dependencies.Projects.SPRING_BOOT_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_CORE_API
import dsl.Dependencies.Projects.SPRING_REST_API

plugins {
    idea
}

dependencies {

    api(project(APPLICATION_CORE_API))
    api(project(SPRING_BOOT_API))
    api(project(AUTH_SERVER_API))
    api(project(SPRING_CONFIG))
    api(project(SPRING_CORE_API))
    api(project(SPRING_REST_API))
    api(project(CACHE_STARTER))
    //implementation(libs.spring.starter)
    implementation(libs.spring.actuator)
    implementation(libs.spring.security)
    implementation(libs.spring.configuration.processor)
    implementation(libs.spring.webflux)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.spring.data.redis)
    implementation(libs.spring.data.redis.reactive)
    implementation(libs.reactor.kotlin.extensions)

    implementation(libs.jboss.logging)
    annotationProcessor(libs.spring.configuration.processor)

    implementation(libs.kotlin.stdlib.jdk8)

}

description = "smart-safe-school-cache-starter"
