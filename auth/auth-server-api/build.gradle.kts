import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.KAFKA_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(KAFKA_API))
    implementation(libs.kotlin.stdlib.jdk8)
}
description = "auth-server-api"
