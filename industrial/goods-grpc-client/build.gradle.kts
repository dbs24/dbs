import dsl.Dependencies.Projects.IND_GOODS_API
import dsl.Dependencies.Projects.IND_GOODS_PROTO_API
import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.GRPC_CLIENT
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(IND_GOODS_API))
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(GRPC_CLIENT))

    implementation(project(IND_GOODS_PROTO_API))
    implementation(libs.log4j.api.kotlin)
    implementation(libs.spring.starter)
    compileOnly(libs.grpc.protobuf)
    compileOnly(libs.grpc.kotlin.stub)
    runtimeOnly(libs.coroutines.core)
    runtimeOnly(libs.coroutines.core.jvm)
    runtimeOnly(libs.coroutines.reactor)

}

description = "goods-grpc-client"
