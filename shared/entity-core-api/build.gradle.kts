import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))

    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.spring.data.r2dbc)
    implementation(libs.log4j.api.kotlin)

}

description = "entity-core-api"
