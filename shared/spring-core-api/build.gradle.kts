import dsl.Dependencies.Projects.APPLICATION_CORE_API

plugins {
    idea
}


dependencies {
    api(project(APPLICATION_CORE_API))

    compileOnly(libs.jjwt.api)

    implementation(libs.spring.webflux)
    implementation(libs.reflection)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.spring.data.commons)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.log4j.core)
    implementation(libs.coroutines.core.jvm)
}

description = "spring-core-api"
