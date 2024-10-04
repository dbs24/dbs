
import dsl.Dependencies.Projects.GRPC_API
import dsl.Dependencies.Projects.PROTOBUF_API_SRC
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(SPRING_CORE_API))
    api(project(GRPC_API))
    implementation(libs.jakarta.annotation.api)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.log4j.api.kotlin)
    implementation(project(PROTOBUF_API_SRC))

    compileOnly(libs.jjwt.api)
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.reactor.kotlin.extensions)
    compileOnly(libs.spring.webflux)
    compileOnly(libs.grpc.protobuf)
    compileOnly(libs.grpc.kotlin.stub)
    compileOnly(libs.coroutines.core)
    runtimeOnly(libs.coroutines.core.jvm)
    runtimeOnly(libs.coroutines.reactor)
    runtimeOnly(libs.grpc.netty)

}

description = "spring-grpc-client-starter"
