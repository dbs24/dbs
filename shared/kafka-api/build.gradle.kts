import dsl.Dependencies.Projects.APPLICATION_CORE_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))

    implementation(libs.spring.json)
    implementation(libs.kotlin.stdlib.common)
}

description = "kafka-api"
