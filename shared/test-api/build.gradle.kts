import dsl.Dependencies.Projects.APPLICATION_CORE_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    implementation(libs.kotlin.stdlib.jdk8)
}

description = "test-api"
