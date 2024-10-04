import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.GRPC_CLIENT
import dsl.Dependencies.Projects.OUT_OF_SERVICE_API
import dsl.Dependencies.Projects.OUT_OF_SERVICE_PROTO_API
import dsl.Dependencies.Projects.PROTOBUF_API_SRC
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(OUT_OF_SERVICE_API))
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(GRPC_CLIENT))
    api(project(PROTOBUF_API_SRC))

    implementation(project(OUT_OF_SERVICE_PROTO_API))
    implementation(libs.log4j.api.kotlin)
    implementation(libs.spring.starter)
    runtimeOnly(libs.coroutines.core)
    runtimeOnly(libs.coroutines.core.jvm)
    runtimeOnly(libs.coroutines.reactor)
    implementation(libs.grpc.all)
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.gen)
}

description = "out-of-service-grpc-client"
