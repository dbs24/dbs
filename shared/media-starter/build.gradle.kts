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

    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.spring.starter)
    implementation(libs.minio)
    //implementation(libs.spring.log4j2)
    implementation(libs.log4j.api.kotlin)

    testApi(libs.spring.starter.test)

}

description = "media-starter"
