
import dsl.Dependencies.Projects.GRPC_API
import dsl.Dependencies.Projects.PROTOBUF_API_SRC
import dsl.Dependencies.Projects.SECURITY_CONFIG_STARTER
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(SPRING_CORE_API))
    api(project(SECURITY_CONFIG_STARTER))
    api(project(GRPC_API))
    implementation(libs.jakarta.annotation.api)
    implementation(libs.spring.starter)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.spring.security)
    implementation(project(PROTOBUF_API_SRC))

    compileOnly(libs.grpc.server.autoconfigure)
    compileOnly(libs.jjwt.api)
    compileOnly(libs.reactor.kotlin.extensions)
    compileOnly(libs.spring.data.r2dbc)
    compileOnly(libs.spring.webflux)
    compileOnly(libs.kotlin.reflect)

    implementation(libs.grpc.all)
    implementation(libs.grpc.gen)
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)

    runtimeOnly(libs.coroutines.reactor)
    testApi(libs.grpc.testing)

}

description = "spring-grpc-server-starter"
