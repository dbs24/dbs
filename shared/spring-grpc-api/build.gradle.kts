
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(SPRING_CORE_API))
    implementation(libs.jakarta.annotation.api)
    implementation(libs.kotlin.stdlib.jdk8)

    compileOnly(libs.coroutines.core)
    compileOnly(libs.jjwt.api)
    compileOnly(libs.spring.starter)
    compileOnly(libs.reactor.kotlin.extensions)
    compileOnly(libs.grpc.protobuf)

}

description = "spring-grpc-api"
