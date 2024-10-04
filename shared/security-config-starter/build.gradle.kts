import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.SECURITY_MANAGER_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_CORE_API
import dsl.Dependencies.Projects.SPRING_REST_API

plugins {
    idea
}

dependencies {

    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(SPRING_CONFIG))
    api(project(SECURITY_MANAGER_API))
    api(project(SPRING_REST_API))

    implementation(libs.spring.security)
    implementation(libs.spring.webflux)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)
    implementation(libs.jwt.auth)
    implementation(libs.nimbus.jose.jwt)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
    compileOnly(libs.kotlin.reflect)

    implementation(libs.kotlin.stdlib.jdk8)

}

description = "security-config-starter"
