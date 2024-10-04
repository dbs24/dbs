import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.KAFKA_API
import dsl.Dependencies.Projects.SPRING_CORE_API
import dsl.Dependencies.Projects.SPRING_REST_API

plugins {
    idea
}


dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(SPRING_REST_API))
    api(project(KAFKA_API))

    implementation(libs.spring.json)
    implementation(libs.kotlin.stdlib.common)
    implementation(libs.log4j.api.kotlin)
}

description = "sandbox-api"
