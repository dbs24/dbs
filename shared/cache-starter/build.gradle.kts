import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_CORE_API

plugins {
    idea
}

dependencies {

    api(project(SPRING_CORE_API))
    api(project(APPLICATION_CORE_API))
    api(project(SPRING_CONFIG))

    implementation(libs.spring.data.redis)
    implementation(libs.spring.data.redis.reactive)
    implementation(libs.spring.redis.session)
    implementation(libs.spring.cache)
    implementation(libs.jedis)
    implementation(libs.log4j.api.kotlin)

    testApi(libs.spring.starter.test)
    implementation(libs.kotlin.stdlib.jdk8)
}

description = "cache-starter"
