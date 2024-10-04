import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.CC_SANDBOX_API
import dsl.Dependencies.Projects.CC_SANDBOX_PROTO_API
import dsl.Dependencies.Projects.GRPC_CLIENT
import dsl.Dependencies.Projects.PROTOBUF_API_SRC
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(CC_SANDBOX_API))
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(PROTOBUF_API_SRC))
    api(project(GRPC_CLIENT))

    implementation(project(CC_SANDBOX_PROTO_API))
    implementation(libs.log4j.api.kotlin)
    implementation(libs.spring.starter)
    compileOnly(libs.grpc.protobuf)
    compileOnly(libs.grpc.kotlin.stub)
    compileOnly(libs.coroutines.core.jvm)
    runtimeOnly(libs.coroutines.core)
    runtimeOnly(libs.coroutines.reactor)

}

description = "sandbox-grpc-client"
