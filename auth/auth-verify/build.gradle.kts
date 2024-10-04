
import dsl.Dependencies.ApplicationAttributes.MAIN_CLASS
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_TITLE
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_VERSION
import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.AUTH_SERVER_API
import dsl.Dependencies.Projects.AUTH_SERVER_CLIENT_API
import dsl.Dependencies.Projects.AUTH_SERVER_PROTO_API
import dsl.Dependencies.Projects.ENTITY_CORE_API
import dsl.Dependencies.Projects.GRPC_HTTP_API
import dsl.Dependencies.Projects.GRPC_SERVER_STARTER
import dsl.Dependencies.Projects.KAFKA_API
import dsl.Dependencies.Projects.NO_VERSION_ASSIGNED
import dsl.Dependencies.Projects.PERSISTENCE_API
import dsl.Dependencies.Projects.PROTOBUF_API_SRC
import dsl.Dependencies.Projects.SECURITY_CONFIG_STARTER
import dsl.Dependencies.Projects.SPRING_BOOT_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_CORE_API
import dsl.Dependencies.Projects.SPRING_KAFKA_STARTER
import dsl.Dependencies.Projects.SPRING_REST_API
import dsl.Dependencies.Projects.STD_LIB
import dsl.Dependencies.Projects.TEST_CORE
import org.gradle.api.file.DuplicatesStrategy.EXCLUDE


plugins {
    idea
    application
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
}

val mainApplicationClassName = "org.dbs.auth.verify.AuthVerifyServer"
val springBootGroup = "org.springframework.boot"
val springDocGroup = "org.springdoc"
val googleGroup = "com.google.code.gson"

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(ENTITY_CORE_API))
    api(project(GRPC_SERVER_STARTER))
    api(project(GRPC_HTTP_API))
    api(project(KAFKA_API))
    api(project(AUTH_SERVER_API))
    api(project(AUTH_SERVER_CLIENT_API))
    api(project(PERSISTENCE_API))
    api(project(SECURITY_CONFIG_STARTER))
    api(project(SPRING_BOOT_API))
    api(project(SPRING_CONFIG))
    api(project(SPRING_CORE_API))
    api(project(SPRING_KAFKA_STARTER))
    api(project(SPRING_REST_API))
    testApi(project(TEST_CORE))

    implementation(kotlin(STD_LIB))
    implementation(libs.bucket4j.core)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)
    implementation(libs.coroutines.reactor)
    implementation(libs.grpc.all)
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.server.spring.boot)
    implementation(libs.jboss.logging)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.log4j.jcl)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.micrometer.prometheus)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.spring.actuator)
    implementation(libs.spring.autoconfigure)
    implementation(libs.spring.configuration.processor)
    implementation(libs.spring.data.r2dbc)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.spring.kafka)
    implementation(libs.spring.security)
    implementation(libs.spring.starter)
    implementation(libs.spring.webflux)
    implementation(libs.springdoc.openapi.webflux.ui)
    implementation(libs.swagger.annotations)
    implementation(platform(libs.protobuf.bom))
    implementation(platform(libs.grpc.bom))
    implementation(platform(libs.kotlin.bom))
    implementation(platform(libs.log4j.bom))
    implementation(platform(libs.spring.dependencies))
    implementation(project(AUTH_SERVER_PROTO_API))
    implementation(project(PROTOBUF_API_SRC))

    runtimeOnly(libs.grpc.all)
    runtimeOnly(libs.grpc.gen)
    runtimeOnly(libs.grpc.netty)
    runtimeOnly(libs.grpc.protoc)
    runtimeOnly(libs.grpc.protoc.kotlin)

    testApi(libs.spring.starter.test)
    testApi(libs.testcontainers.junit.jupiter)

    annotationProcessor(libs.spring.configuration.processor)
    annotationProcessor(libs.spring.autoconfigure.processor)
}

springBoot {
    buildInfo()
    mainClass = mainApplicationClassName
}

configurations {
    all {
        exclude(group = springBootGroup, module = "spring-boot-starter-logging")
        exclude(group = springBootGroup, module = "junit-vintage-engine")
        exclude(group = springDocGroup, module = "jackson-core")
        //exclude(group = googleGroup, module = "gson")
    }
}

tasks.jar {
    manifest {
        attributes[MAIN_CLASS] = mainApplicationClassName
        attributes[IMPLEMENTATION_TITLE] = project.name
        attributes[IMPLEMENTATION_VERSION] = project.version
    }

    project.version = NO_VERSION_ASSIGNED
    //project.setProperty(MAIN_CLASS_NAME, mainApplicationClassName)
    archiveBaseName.set(project.name)

    isZip64 = true
    duplicatesStrategy = EXCLUDE

}


description = "auth verify server"

tasks.test {
    useJUnitPlatform()
}
