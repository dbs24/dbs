
import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.AUTH_SERVER_API
import dsl.Dependencies.Projects.AUTH_SERVER_PROTO_API
import dsl.Dependencies.Projects.GRPC_CLIENT
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(AUTH_SERVER_API))
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(GRPC_CLIENT))

    implementation(project(AUTH_SERVER_PROTO_API))
    implementation(libs.log4j.api.kotlin)
    implementation(libs.spring.starter)
    runtimeOnly(libs.coroutines.core)
    runtimeOnly(libs.coroutines.core.jvm)
    runtimeOnly(libs.coroutines.reactor)
    implementation(libs.grpc.all)
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.gen)
}

description = "auth-proto-client"
