import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    //exclude(group = googleGroup, module = "gson")
    implementation(libs.spring.webflux)
    implementation(libs.spring.data.jdbc)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.log4j.api.kotlin)

}

description = "persistence-api"
