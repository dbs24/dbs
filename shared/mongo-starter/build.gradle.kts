import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(SPRING_CONFIG))
    //api(project(CACHE_STARTER)

    implementation(libs.spring.mongodb.reactive)
//    implementation(libs.spring.data.r2dbc)
//    implementation(libs.mongodb.core)
//    implementation(libs.mongodb.reactivestreams)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.log4j.api.kotlin)
}

description = "mongo-starter"
