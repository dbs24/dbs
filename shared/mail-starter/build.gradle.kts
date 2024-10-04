import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.KAFKA_API
import dsl.Dependencies.Projects.SPRING_CORE_API
import dsl.Dependencies.Projects.SPRING_KAFKA_STARTER

plugins {
    idea
}

dependencies {

    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CORE_API))
    api(project(KAFKA_API))
    api(project(SPRING_KAFKA_STARTER))

    implementation(libs.spring.kafka)
    implementation(libs.spring.autoconfigure)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.stdlib.common)
    implementation(libs.log4j.api.kotlin)
}

description = "mail-starter"
