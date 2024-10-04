import dsl.Dependencies.Projects.APPLICATION_CORE_API

plugins {
    idea
}

description = "security-manager-api"

dependencies {
    api(project(APPLICATION_CORE_API))
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.jackson.module.kotlin)
}
