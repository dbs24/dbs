import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(SPRING_CORE_API))

    implementation(libs.spring.starter)
    implementation(libs.spring.data.commons)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.log4j.api.kotlin)
}

description = "ref-starter"
