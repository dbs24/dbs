import dsl.Dependencies.Projects.TEST_API

plugins {
    idea
}

dependencies {
    api(project(TEST_API))
    implementation(libs.kotlin.stdlib.jdk8)
}

description = "test-junit-api"
