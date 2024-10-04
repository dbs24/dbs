import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.PROTOBUF_API_SRC
import dsl.Dependencies.Projects.SPRING_REST_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_REST_API))
    implementation(libs.log4j.api.kotlin)
    compileOnly(libs.grpc.all)
    compileOnly(libs.reactor.kotlin.extensions)
    compileOnly(libs.spring.webflux)
    compileOnly(libs.kotlin.reflect)
    implementation(project(PROTOBUF_API_SRC))
}

description = "grpc-http-api"
