import dsl.Dependencies.Projects.TT_MGMT_API
import dsl.Dependencies.Projects.TT_MGMT_PROTO_API
import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.GRPC_CLIENT
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(TT_MGMT_API))
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(GRPC_CLIENT))

    implementation(project(TT_MGMT_PROTO_API))
    implementation(libs.log4j.api.kotlin)
    implementation(libs.spring.starter)
    compileOnly(libs.grpc.protobuf)
    compileOnly(libs.grpc.kotlin.stub)
    runtimeOnly(libs.coroutines.core)
    runtimeOnly(libs.coroutines.core.jvm)
    runtimeOnly(libs.coroutines.reactor)

}

description = "tt-mgmt-grpc-client"
