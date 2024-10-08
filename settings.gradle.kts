import org.gradle.api.JavaVersion.VERSION_21
import org.gradle.api.JavaVersion.current
import java.text.NumberFormat
import java.util.*

pluginManagement {
    val kotlinVersion: String by settings
    val springBootLibVersion: String by settings
    val springReleaseVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val springJavadocAggregateVersion: String by settings
    val reflectoringSpringBootDevtoolsVersion: String by settings
    val jetbrainsGradlePluginIdeaExt: String by settings
    val gradleWrapperUpgrade: String by settings
    val swaggerGradlePlugin: String by settings
    val gradleProjectEnforcer: String by settings
    val openapiSpringGenerator: String by settings
    val autonomousappsPluginBestPractices: String by settings
    val googleProtobufPlugin: String by settings
    val googleProtobufCore: String by settings
    val kordampGradle: String by settings
    val johnRengelman: String by settings
    val benManesVersion: String by settings
    val detektPluginLibVersion: String by settings
    val cryptoChecksumVersion: String by settings
    val javaGradlePluginVersion: String by settings
    val graalvmBuildToolVersion: String by settings
    val gradleTestRetryPluginVersion: String by settings
    val diffPlugSpotlessVersion: String by settings
    val intellijPluginVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("plugin.scripting") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        kotlin("plugin.noarg") version kotlinVersion
        kotlin("kapt") version kotlinVersion
        id("org.jetbrains.intellij") version intellijPluginVersion
        //
        id("org.springframework.boot") version springBootLibVersion
        id("io.spring.release") version springReleaseVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("io.spring.javadoc-aggregate") version springJavadocAggregateVersion
        id("io.reflectoring.spring-boot-devtools") version reflectoringSpringBootDevtoolsVersion
        id("org.jetbrains.gradle.plugin.idea-ext") version jetbrainsGradlePluginIdeaExt
        id("io.swagger.core.v3.swagger-gradle-plugin") version swaggerGradlePlugin
        id("org.kordamp.gradle.project-enforcer") version gradleProjectEnforcer
        id("com.github.slamdev.openapi-spring-generator") version openapiSpringGenerator
        id("com.autonomousapps.plugin-best-practices-plugin") version autonomousappsPluginBestPractices
        id("com.google.protobuf") version googleProtobufPlugin
        id("org.kordamp.gradle.project") version kordampGradle
        id("com.github.johnrengelman.shadow") version johnRengelman
        id("com.github.ben-manes.versions") version benManesVersion
        //id("org.graalvm.buildtools.native") version graalvmBuildToolVersion
        id("io.gitlab.arturbosch.detekt") version detektPluginLibVersion
        // gradle
        id("org.gradle.wrapper-upgrade") version gradleWrapperUpgrade
        id("dev.gradleplugins.java-gradle-plugin") version javaGradlePluginVersion
        id("org.gradle.test-retry") version gradleTestRetryPluginVersion
        id("org.gradle.crypto.checksum") version cryptoChecksumVersion
        // other
        id("com.diffplug.spotless") version diffPlugSpotlessVersion
    }
}

rootProject.name = "dbs"

// include subprojects
val buildSrcFolder = "buildSrc"
val projectSubFolder = "shared"
val deprecatedFolder = "deprecated"
val resourceFolder = "src/main/resources"
val projectColonSubFolder = ":$projectSubFolder"
val buildFile = "build.gradle.kts"
var projectsCount = 0

// register projects and modules
//======================================================================================================================
//  temporary exclusions

val excludeProjectsList = setOf(
    "faked-project",
    "p-cm",
)

val mandatoryProjectsList = setOf(
    "faked-project",
)
//======================================================================================================================

rootDir
    .walk()
    .maxDepth(4)
    .filter { project ->
        project.name != buildSrcFolder
                && (!project.name.contains("sss"))
                && project.isDirectory
                && !project.absolutePath.contains(deprecatedFolder)
                && file("${project.absolutePath}/$buildFile").exists()
                && ((!excludeProjectsList.any { project.absolutePath.contains(it) }
                || mandatoryProjectsList.any { project.absolutePath.contains(it) })
            .also {
                if (!it) {
                    println("### ignore project [${project.name}] (${project.absolutePath})")
                }
            })
    }
    .map { it.absolutePath.substring(rootDir.absolutePath.length).replace("/", ":") }
    .filter { it.isNotEmpty() }
    .sortedBy { (if (it.startsWith(projectColonSubFolder)) "0" else "1").plus(it) }
    .forEach {
        println("include project [${++projectsCount}] $it")
        include(it)
    }

// copy certs to project folders
//======================================================================================================================
val domainName = "k11dev.tech"
val sslSrcFolder = "certs/$domainName"
val sslCertName = "$domainName.jks"
val sslKeyCertName = "$domainName.key"
val sslCertChainName = "$domainName.pem"
val bannerFileName = "banner.txt"
val sslCertSrcFileName = "$rootDir/$sslSrcFolder/$sslCertName"
val sslKeyCertSrcFileName = "$rootDir/$sslSrcFolder/$sslKeyCertName"
val sslCertChainSrcFileName = "$rootDir/$sslSrcFolder/$sslCertChainName"
val sslCertSrcFile = file(sslCertSrcFileName)
val sslKeyCertSrcFile = file(sslKeyCertSrcFileName)
val sslCertChainSrcFile = file(sslCertChainSrcFileName)

fun copyCert(absolutePath: String, srcFile: File) {

    if (!srcFile.exists()) {
        throw NoSuchElementException("certs file does not exists: '$srcFile'")
    }
    //println("source ssl cert: $srcFile, lastModified: ${srcFile.lastModified()}")

    val fileName = srcFile.name
    val sslCertSrcFileLastModified = srcFile.lastModified()
    val dstSslFileName = "$absolutePath/$resourceFolder/$fileName"
    val sslDstCertFile = file(dstSslFileName)
    val needUpdateSslCert = if (sslDstCertFile.exists()) {
        sslDstCertFile.lastModified() != sslCertSrcFileLastModified
    } else {
        true
    }
    if (needUpdateSslCert) {
        println("refresh ssl cert: $dstSslFileName, lastModified: ${sslDstCertFile.lastModified()}")
        if (sslDstCertFile.exists()) {
            sslDstCertFile.delete()
        }
        srcFile.copyTo(sslDstCertFile, true)
        sslDstCertFile.setLastModified(sslCertSrcFileLastModified)
    }
}
//======================================================================================================================

rootDir
    .walk()
    .maxDepth(3)
    .filter {
        it.name != buildSrcFolder
                && it.isDirectory
                && file("${it.absolutePath}/$resourceFolder").exists()
                && !it.absolutePath.contains(deprecatedFolder)
                && !it.name.endsWith("-api")
                && !it.name.endsWith("-starter")
                && !it.name.endsWith("-core")
                && !it.name.endsWith("-ets")
    }
    .forEach {
//        val dstSslFileName = "${it.absolutePath}/$resourceFolder/$sslCertName"
        copyCert(it.absolutePath, sslCertSrcFile)
        copyCert(it.absolutePath, sslKeyCertSrcFile)
        copyCert(it.absolutePath, sslCertChainSrcFile)

    }
//======================================================================================================================
buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val springBootLibVersion: String by settings
            val springBootDataLibVersion: String by settings
            val springBootSessionLibVersion: String by settings
            val springCloudLibVersion: String by settings
            val springCloudBalancerLibVersion: String by settings
            val springCloudDependenciesLibVersion: String by settings
            val springSecurityLibVersion: String by settings
            val springKafkaLibVersion: String by settings
            val redisClientsLibVersion: String by settings
            val jakartaValidationLibVersion: String by settings
            val micrometerPrometheusLibVersion: String by settings
            val fasterXmlJacksonDataformatLibVersion: String by settings
            val fasterXmlJacksonModuleLibVersion: String by settings
            val reflectionsLinVersion: String by settings
            val jwtLibVersion: String by settings
            val jjwtApiLibVersion: String by settings
            val swaggerCoreV3libVersion: String by settings
            val springdocLibVersion: String by settings
            val kotlinVersion: String by settings
            val jbossLoggingLibVersion: String by settings
            val apacheCommonsLibVersion: String by settings
            val apacheCommonsCollections4LibVersion: String by settings
            val nimbusJoseJwtLibVersion: String by settings
            val postgresLibVersion: String by settings
            val mongoLibVersion: String by settings
            val openJdkJmhLibVersion: String by settings
            val queryDslLibVersion: String by settings
            val r2dbcPoolLibVersion: String by settings
            val r2dbcPostgresLibVersion: String by settings
            val reactorToolsLibVersion: String by settings
            val reactorNettyLibVersion: String by settings
            val blockhoundJunitPlatformLibVersion: String by settings
            val reactorKotlinExtensionsLibVersion: String by settings
            val testcontainersLibVersion: String by settings
            val kotestLibVersion: String by settings
            val kotestAssertionJvmLibVersion: String by settings
            val kotestCoreLibJvmVersion: String by settings
            val kotestSpringExtensionLibVersion: String by settings
            val kotestTestContainersExtensionLibVersion: String by settings
            val yamlLibVersion: String by settings
            val grpcLibVersion: String by settings
            val grpcGateWayLibVersion: String by settings
            val grpcKotlinStubLibVersion: String by settings
            val googleProtobufLibVersion: String by settings
            val grpcServerSpringBootAutoconfigureLibVersion: String by settings
            val grpcServerSpringBootLibVersion: String by settings
            val grpcSpringBootLibVersion: String by settings
            val jakartaAnnotationLibVersion: String by settings
            val minioGroupLibVersion: String by settings
            val mockitoGroupLibVersion: String by settings
            val mockitoGroupInlineLibVersion: String by settings
            val kotlinxLibVersion: String by settings
            val log4jApiKotlinLibVersion: String by settings
            val log4jApiLibVersion: String by settings
            val bucket4jLibVersion: String by settings
            val jbossMarshallingLibVersion: String by settings
            val reactorTestLibVersion: String by settings
            val jsoupLibVersion: String by settings
            val kotlinSerializationLibVersion: String by settings
            val kotlinxLincheckLibVersion: String by settings
            val detektPluginLibVersion: String by settings
            val neatChessLibVersion: String by settings
            val googleFirebaseLibVersion: String by settings
            val applePushyLibVersion: String by settings
            val deviceDetectorLibVersion: String by settings
            val stripePaymentsLibVersion: String by settings
            val googleGsonLibVersion: String by settings

            // groups
            val springBootGroup = "org.springframework.boot"
            val springBootSessionGroup = "org.springframework.session"
            val springBootDataGroup = "org.springframework.data"
            // version catalog
            val googleProtobufVersion = "com.google.protobuf.version"
            val springBootVersion = "spring.boot.version"
            val springBootSessionVersion = "spring.boot.session.version"
            val springBootDataVersion = "spring.boot.data.version"
            val springCloudVersion = "spring.cloud.version"
            //val springCloudBalancerVersion = "spring.cloud.balancer.version"
            //val springBootCloudGroup = "org.springframework.cloud"
            //val springCloudDependenciesVersion = "spring.cloud.dependencies.version"
            val springSecurityVersion = "spring.security.version"
            val springKafkaVersion = "spring.kafka.version"
            val redisClientsVersion = "redis.clients.version"
            val micrometerPrometheusVersion = "micrometer.prometheus.version"
            val openJdkJmhVersion = "openjdk.jmh.version"
            val fasterXmlJacksonDataformatVersion = "fasterxml.jackson.dataformat.version"
            val fasterXmlJacksonModuleVersion = "fasterxml.jackson.module.version"
            val reflectionsVersion = "reflections.version"
            val jwtVersion = "jwt.version"
            val jjwtApiVersion = "jjwt.api.version"
            val swaggerCoreV3Version = "swagger.core.v3.version"
            val springdocVersion = "springdoc.version"
            val jbossLoggingVersion = "jboss.logging.version"
            val nimbusJoseJwtVersion = "nimbus.jose.jwt.version"
            val postgresVersion = "postgresql.version"
            val mongoVersion = "mongodb.version"
            val r2dbcPoolVersion = "r2dbc.pool.version"
            val r2dbcPostgresVersion = "r2dbc.postgres.version"
            val reactorToolsVersion = "reactor.tools.version"
            val reactorTestVersion = "reactor.test.version"
            val reactorNettyVersion = "reactor.netty.version"
            val blockhoundJunitPlatformVersion = "blockhound.junit.platform.version"
            val testcontainersVersion = "testcontainers.version"
            val yamlVersion = "yaml.version"
            val grpcVersion = "grpc.version"
            val grpcGateWayVersion = "grpc.gateway.version"
            val grpcKotlinStubVersion = "grpc.kotlin.stub.version"
            val grpcServerSpringBootVersion = "grpc.server.spring.boot.version"
            val grpcServerSpringBootAutoconfigureVersion  = "grpc.server.spring.boot.autoconfigure.version"
            val grpcSpringBootVersion = "grpc.spring.boot.version"
            val jakartaAnnotationVersion = "jakarta.annotation.version"
            val jakartaValidationVersion = "jakarta.validation.version"
            val jetbrainsKotlinVersion = "jetbrains.kotlin.version"
            val jetbrainsKotlinReflectVersion = "kotlin.reflect.version"
            val jetbrainsKotlinNoArgVersion = "kotlin.noarg.version"
            val springBootKafkaGroup = "org.springframework.kafka"
            val springBootSecurityGroup = "org.springframework.security"
            val springDocGroup = "org.springdoc"
            val springDevh = "net.devh"
            val springGrpc = "io.grpc"
            val micrometer = "io.micrometer"
            val openJdkJmh = "org.openjdk.jmh"
            val springFasterXmlGroup = "com.fasterxml.jackson.core"
            val projectReactorGroup = "io.projectreactor"
            val projectReactorNettyGroup = "io.projectreactor.netty"
            val jsonWebTokenGroup = "io.jsonwebtoken"
            val reactorKotlinExtensionsVersion = "io.projectreactor.kotlin"
            val minioGroupVersion = "io.minio"
            val kotlinxVersion = "jetbrains.kotlinx.version"
            val kotlinTestJunitVersion = "kotlin.test.junit.version"
            val kotestVersion = "kotest.assertion.version"
            val kotestAssertionJvmVersion = "kotest.assertion.jvm.version"
            val kotestCoreJvmVersion = "kotest.core.jvm.version"
            val kotestSpringExtensionVersion = "kotest.spring.version"
            val kotestTestContainersExtensionVersion = "kotest.testcontainers.version"
            val mockitoGroupVersion = "mockito.version"
            val mockitoGroupInlineVersion = "mockito.inline.version"
            val queryDslVersion = "query.dsl.version"
            val log4jApiKotlinVersion = "log4j.api.kotlin.version"
            val log4jApiVersion = "log4j.api.version"
            val bucket4jVersion = "bucket4j.version"
            val jbossMarshallingVersion = "jboss.marshalling.version"
            val jsoupVersion = "org.jsoup.version"
            val kotlinSerializationVersion = "kotlin.serialization.version"
            val kotlinxLincheckVersion = "kotlinx.lincheck.version"
            val detektPluginVersion = "detekt.version"
            val neatChessVersion = "net.anreinc.neatchess.version"
            val googleFirebaseVersion = "google.firebase"
            val applePushyVersion = "apple.pushy"
            val deviceDetectorVersion = "github.devicedetector"
            val stripePaymentsVersion = "stripe.payments.lib"
            val googleGsonVersion = "google.gson.version"

            val runTime = Runtime.getRuntime()
            val numberFormat = NumberFormat.getInstance(Locale("en"))

            println("-------------------------------------------------------------------------------------------------")
            println("springBootLibVersion: $springBootLibVersion, kotlinVersion: $kotlinVersion, " +
                    "JVM version: ${current()}, processors|threads: ${runTime.availableProcessors()}")
            println("gradleVersion: ${gradle.gradleVersion}, gradleProperties = ${gradle.extra.properties.entries}")
            println("gradle total Memory: ${numberFormat.format(runTime.totalMemory())} bytes"
                .plus(", free Memory: ${numberFormat.format(runTime.freeMemory())} bytes"))
            println("-------------------------------------------------------------------------------------------------")

            check(current().isCompatibleWith(VERSION_21)) {
                "This project needs to be run with Java 21 or higher (found: ${current()})" }

            // spring-boot
            version(springBootVersion, springBootLibVersion)
            library("spring-boot", springBootGroup, "spring-boot").versionRef(springBootVersion)
            //library("spring-aop", springBootGroup, "spring-boot-starter-aop").versionRef(springBootVersion)
            library("spring-boot-parent", springBootGroup, "spring-boot-starter-parent").versionRef(springBootVersion)
            library("spring-dependencies", springBootGroup, "spring-boot-dependencies").versionRef(springBootVersion)
            library("spring-starter", springBootGroup, "spring-boot-starter").versionRef(springBootVersion)
            library("spring-webflux", springBootGroup, "spring-boot-starter-webflux").versionRef(springBootVersion)
            library("spring-data-jpa", springBootGroup, "spring-boot-starter-data-jpa").versionRef(springBootVersion)
            library("spring-validation", springBootGroup,"spring-boot-starter-validation").versionRef(springBootVersion)
            library("spring-security", springBootGroup, "spring-boot-starter-security").versionRef(springBootVersion)
            library("spring-actuator", springBootGroup, "spring-boot-starter-actuator").versionRef(springBootVersion)
            library("spring-data-jdbc", springBootGroup, "spring-boot-starter-data-jdbc").versionRef(springBootVersion)
            library("spring-data-r2dbc", springBootGroup, "spring-boot-starter-data-r2dbc").versionRef(springBootVersion)
            library("spring-data-redis", springBootGroup, "spring-boot-starter-data-redis").versionRef(springBootVersion)
            library("spring-devtools", springBootGroup, "spring-boot-devtools").versionRef(springBootVersion)
            library("spring-mongodb", springBootGroup, "spring-boot-starter-data-mongodb").versionRef(springBootVersion)
            library("spring-mongodb-reactive", springBootGroup, "spring-boot-starter-data-mongodb-reactive").versionRef(springBootVersion)
            library("spring-data-redis-reactive", springBootGroup,"spring-boot-starter-data-redis-reactive").versionRef(springBootVersion)
            library("spring-cache", springBootGroup, "spring-boot-starter-cache").versionRef(springBootVersion)
            library("spring-json", springBootGroup, "spring-boot-starter-json").versionRef(springBootVersion)
            library("spring-configuration-processor", springBootGroup,"spring-boot-configuration-processor").versionRef(springBootVersion)
            library("spring-autoconfigure-processor", springBootGroup,"spring-boot-autoconfigure-processor").versionRef(springBootVersion)
            library("spring-web", springBootGroup, "spring-boot-starter-web").versionRef(springBootVersion)
            library("spring-autoconfigure", springBootGroup, "spring-boot-autoconfigure").versionRef(springBootVersion)
            library("spring-mail", springBootGroup, "spring-boot-starter-mail").versionRef(springBootVersion)
            library("spring-thymeleaf", springBootGroup, "spring-boot-starter-thymeleaf").versionRef(springBootVersion)
            // spring-boot-session
            library("spring-redis-session",springBootSessionGroup,"spring-session-data-redis").versionRef(springBootSessionVersion)
            version(springBootSessionVersion, springBootSessionLibVersion)
            // spring-boot-test
            library("spring-starter-test", springBootGroup, "spring-boot-starter-test").versionRef(springBootVersion)
            library("spring-starter-test-autoconfigure", springBootGroup, "spring-boot-test-autoconfigure").versionRef(springBootVersion)
            // spring-data
            version(springBootDataVersion, springBootDataLibVersion)
            library("spring-data-commons", springBootDataGroup, "spring-data-commons").versionRef(springBootDataVersion)
            // spring-cloud
            version(springCloudVersion, springCloudLibVersion)
            //library("spring-cloud-starter-gateway", springBootCloudGroup, "spring-cloud-starter-gateway").versionRef(springCloudVersion)
            //==========================================================================================================
            //==========================================================================================================
            //library("spring-cloud-starter-netflix-eureka-server", springBootCloudGroup, "spring-cloud-starter-netflix-eureka-server").versionRef(springCloudVersion)
            //library("spring-cloud-netflix-eureka-client", springBootCloudGroup, "spring-cloud-starter-netflix-eureka-client").versionRef(springCloudVersion)
            // spring-cloud-loadbalancer
            //version(springCloudBalancerVersion, springCloudBalancerLibVersion)
            //library("spring-cloud-starter-loadbalancer", springBootCloudGroup,"spring-cloud-starter-loadbalancer").versionRef(springCloudBalancerVersion)
            //library("spring-cloud-loadbalancer", springBootCloudGroup, "spring-cloud-loadbalancer").versionRef(springCloudBalancerVersion)
            // spring-cloud-dependencies
            //version(springCloudDependenciesVersion, springCloudDependenciesLibVersion)
            //library("spring-cloud-dependencies", springBootCloudGroup, "spring-cloud-dependencies").versionRef(springCloudDependenciesVersion)
            // spring-security
            version(springSecurityVersion, springSecurityLibVersion)
            library("spring-security-jwt", springBootSecurityGroup, "spring-security-jwt").versionRef(springSecurityVersion)
            // kafka
            version(springKafkaVersion, springKafkaLibVersion)
            library("spring-kafka", springBootKafkaGroup, "spring-kafka").versionRef(springKafkaVersion)
            // redis
            version(redisClientsVersion, redisClientsLibVersion)
            library("jedis", "redis.clients", "jedis").versionRef(redisClientsVersion)
            // jakarta
            version(jakartaValidationVersion, jakartaValidationLibVersion)
            library("jakarta-validation", "jakarta.validation", "jakarta.validation-api").versionRef(jakartaValidationVersion)
            // metrics
            version(micrometerPrometheusVersion, micrometerPrometheusLibVersion)
            library("micrometer-prometheus", micrometer, "micrometer-registry-prometheus").versionRef(micrometerPrometheusVersion)
            // jmh
            version(openJdkJmhVersion, openJdkJmhLibVersion)
            library("jmh-core", openJdkJmh, "jmh-core").versionRef(openJdkJmhVersion)
            library("jmh-annotations", openJdkJmh, "jmh-generator-annprocess").versionRef(openJdkJmhVersion)
            // jackson.dataformat
            version(fasterXmlJacksonDataformatVersion, fasterXmlJacksonDataformatLibVersion)
            library("jackson-dataformat-xml", "com.fasterxml.jackson.dataformat", "jackson-dataformat-xml").versionRef(fasterXmlJacksonDataformatVersion)
            // jackson.module
            version(fasterXmlJacksonModuleVersion, fasterXmlJacksonModuleLibVersion)
            library("jackson-module-kotlin", "com.fasterxml.jackson.module", "jackson-module-kotlin").versionRef(fasterXmlJacksonModuleVersion)
            // reflections
            version(reflectionsVersion, reflectionsLinVersion)
            library("reflection", "org.reflections", "reflections").versionRef(reflectionsVersion)
            // jwt
            version(jwtVersion, jwtLibVersion)
            library("jwt.auth", "com.auth0", "java-jwt").versionRef(jwtVersion)
            // jjwt.api
            version(jjwtApiVersion, jjwtApiLibVersion)
            library("jjwt-api", "io.jsonwebtoken", "jjwt-api").versionRef(jjwtApiVersion)
            library("jjwt-impl", "io.jsonwebtoken", "jjwt-impl").versionRef(jjwtApiVersion)
            library("jjwt-jackson", "io.jsonwebtoken", "jjwt-jackson").versionRef(jjwtApiVersion)
            // swagger
            version(swaggerCoreV3Version, swaggerCoreV3libVersion)
            library("swagger-annotations", "io.swagger.core.v3", "swagger-annotations").versionRef(swaggerCoreV3Version)
            // springdoc
            version(springdocVersion, springdocLibVersion)
            library("springdoc-openapi-webflux-ui", springDocGroup, "springdoc-openapi-starter-webflux-ui").versionRef(springdocVersion)
            library("springdoc-openapi-kotlin", springDocGroup, "springdoc-openapi-kotlin-starter-common").versionRef(springdocVersion)
            // jboss
            version(jbossLoggingVersion, jbossLoggingLibVersion)
            library("jboss-logging", "org.jboss.logging", "jboss-logging").versionRef(jbossLoggingVersion)
            version(jbossMarshallingVersion, jbossMarshallingLibVersion)
            library("jboss-marshalling", "org.jboss.marshalling", "jboss-marshalling").versionRef(
                jbossMarshallingVersion
            )
            // nimbusds
            version(nimbusJoseJwtVersion, nimbusJoseJwtLibVersion)
            library("nimbus-jose-jwt", "com.nimbusds", "nimbus-jose-jwt").versionRef(nimbusJoseJwtVersion)
            // postgresql
            version(postgresVersion, postgresLibVersion)
            library("postgresql", "org.postgresql", "postgresql").versionRef(postgresVersion)
            // querydsl
            version(queryDslVersion, queryDslLibVersion)
            library("querydsl-mongodb", "com.querydsl", "querydsl-mongodb").versionRef(queryDslVersion)
            library("querydsl-apt", "com.querydsl", "querydsl-apt").versionRef(queryDslVersion)
            // r2dbc.pool
            version(r2dbcPoolVersion, r2dbcPoolLibVersion)
            library("r2dbc.pool", "io.r2dbc", "r2dbc-pool").versionRef(r2dbcPoolVersion)
            // r2dbc.postgres
            version(r2dbcPostgresVersion, r2dbcPostgresLibVersion)
            library("r2dbc.postgresql", "org.postgresql", "r2dbc-postgresql").versionRef(r2dbcPostgresVersion)
            // project.reactor
            version(reactorToolsVersion, reactorToolsLibVersion)
            library("reactor.tools", projectReactorGroup, "reactor-tools").versionRef(reactorToolsVersion)
            // project.reactor.test
            version(reactorTestVersion, reactorTestLibVersion)
            library("reactor.test", "io.projectreactor", "reactor-test").versionRef(reactorTestVersion)
            // project.reactor.tools
            version(blockhoundJunitPlatformVersion, blockhoundJunitPlatformLibVersion)
            library("blockhound", "io.projectreactor.tools", "blockhound").versionRef(blockhoundJunitPlatformVersion)
            library("blockhound-junit-platform", "io.projectreactor.tools", "blockhound-junit-platform").versionRef(blockhoundJunitPlatformVersion)
            // projectreactor.netty
            version(reactorNettyVersion, reactorNettyLibVersion)
            library("reactor-netty", "io.projectreactor.netty", "reactor-netty").versionRef(reactorNettyVersion)
            // projectreactor.kotlin.extension
            version(reactorKotlinExtensionsVersion, reactorKotlinExtensionsLibVersion)
            library("reactor-kotlin-extensions", "io.projectreactor.kotlin", "reactor-kotlin-extensions").versionRef(reactorKotlinExtensionsVersion)
            // testcontainers
            version(testcontainersVersion, testcontainersLibVersion)
            library("testcontainers", "org.testcontainers", "testcontainers").versionRef(testcontainersVersion)
            library("testcontainers-junit-jupiter", "org.testcontainers", "junit-jupiter").versionRef(testcontainersVersion)
            library("testcontainers-postgresql", "org.testcontainers", "postgresql").versionRef(testcontainersVersion)
            library("testcontainers-kafka", "org.testcontainers", "kafka").versionRef(testcontainersVersion)
            library("testcontainers-r2dbc", "org.testcontainers", "r2dbc").versionRef(testcontainersVersion)
            // kotlin-test
            version(kotlinTestJunitVersion, kotlinVersion)
            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test").versionRef(kotlinTestJunitVersion)
            library("kotlin-test-junit", "org.jetbrains.kotlin", "kotlin-test-junit").versionRef(kotlinTestJunitVersion)
            // kotest
            version(kotestVersion, kotestLibVersion)
            library("kotest-bom", "io.kotest", "kotest-bom").versionRef(kotestVersion)
            library("kotest-assertions-core", "io.kotest", "kotest-assertions-core").versionRef(kotestVersion)
            library("kotest-assertions-core-jvm", "io.kotest", "kotest-assertions-core-jvm").versionRef(kotestVersion)
            library("kotest-runner-junit5-jvm", "io.kotest", "kotest-runner-junit5-jvm").versionRef(kotestVersion)
            library("kotest-property-jvm", "io.kotest", "kotest-property-jvm").versionRef(kotestVersion)
            library("kotest-common-jvm", "io.kotest", "kotest-common-jvm").versionRef(kotestVersion)
            // kotest extension
            library("kotest-blockhound", "io.kotest", "kotest-extensions-blockhound").versionRef(kotestVersion)
            // mockito
            version(mockitoGroupVersion, mockitoGroupLibVersion)
            library("mockito-core", "org.mockito", "mockito-core").versionRef(mockitoGroupVersion)
            library("mockito-junit-jupiter", "org.mockito", "mockito-junit-jupiter").versionRef(mockitoGroupVersion)
            // mockito-inline
            version(mockitoGroupInlineVersion, mockitoGroupInlineLibVersion)
            library("mockito-inline", "org.mockito", "mockito-inline").versionRef(mockitoGroupInlineVersion)
            // kotest - assertion - jvm
            version(kotestAssertionJvmVersion, kotestAssertionJvmLibVersion)
            library("kotest-assertions-jvm", "io.kotest", "kotest-assertions-jvm").versionRef(kotestAssertionJvmVersion)
            // kotest - core - jvm
            version(kotestCoreJvmVersion, kotestCoreLibJvmVersion)
            library("kotest-core-jvm", "io.kotest", "kotest-core-jvm").versionRef(kotestCoreJvmVersion)
            // kotest - spring
            version(kotestSpringExtensionVersion, kotestSpringExtensionLibVersion)
            library("kotest-extensions-spring", "io.kotest.extensions", "kotest-extensions-spring").versionRef(kotestSpringExtensionVersion)
            // kotest - testcontainers
            version(kotestTestContainersExtensionVersion, kotestTestContainersExtensionLibVersion)
            library("kotest-testcontainers", "io.kotest.extensions", "kotest-extensions-testcontainers").versionRef(kotestTestContainersExtensionVersion)
            // kotlin-jacoco
            library("kotlin-jacoco", "org.jetbrains.kotlin", "kotlin-gradle-plugin").versionRef(kotlinTestJunitVersion)
            // snakeYaml
            version(yamlVersion, yamlLibVersion)
            library("snakeyaml", "org.yaml", "snakeyaml").versionRef(yamlVersion)
            // protobuf
            version(googleProtobufVersion, googleProtobufLibVersion )
            library("protobuf-bom", "com.google.protobuf", "protobuf-bom").versionRef(googleProtobufVersion)
            library("grpc-protoc", "com.google.protobuf", "protoc").versionRef(googleProtobufVersion)
            library("protobuf-kotlin-lite", "com.google.protobuf", "protobuf-kotlin-lite").versionRef(googleProtobufVersion)
            library("protobuf-java", "com.google.protobuf", "protobuf-java").versionRef(googleProtobufVersion)
            // grpc generic
            version(grpcVersion, grpcLibVersion)
            library("grpc-bom", "io.grpc", "grpc-bom").versionRef(grpcVersion)
            library("grpc-api", "io.grpc", "grpc-api").versionRef(grpcVersion)
            library("grpc-all", "io.grpc", "grpc-all").versionRef(grpcVersion)
            library("grpc-protobuf", "io.grpc", "grpc-protobuf").versionRef(grpcVersion)
            library("grpc-googleapis", "io.grpc", "grpc-googleapis").versionRef(grpcVersion)
            library("grpc-gen", "io.grpc", "protoc-gen-grpc-java").versionRef(grpcVersion)
            library("grpc-netty", "io.grpc", "grpc-netty").versionRef(grpcVersion)
            library("grpc-stub", "io.grpc", "grpc-stub").versionRef(grpcVersion)
            library("grpc-protobuf-lite", "io.grpc", "grpc-protobuf-lite").versionRef(grpcVersion)
            library("grpc-testing", "io.grpc", "grpc-testing").versionRef(grpcVersion)
            // grpc kotlin
            version(grpcKotlinStubVersion, grpcKotlinStubLibVersion)
            library("grpc-kotlin-stub", "io.grpc", "grpc-kotlin-stub").versionRef(grpcKotlinStubVersion)
            library("grpc-protoc-kotlin", "io.grpc", "protoc-gen-grpc-kotlin").versionRef(grpcKotlinStubVersion)
            // grpc-gateway
            version(grpcGateWayVersion, grpcGateWayLibVersion)
            library("grpc-gateway", "com.google.api.grpc", "grpc-google-cloud-api-gateway-v1").versionRef(grpcGateWayVersion)
            // grpc-server-spring-boot
            version(grpcServerSpringBootVersion, grpcServerSpringBootLibVersion)
            library("grpc-server-spring-boot", "net.devh", "grpc-server-spring-boot-starter").versionRef(grpcServerSpringBootVersion)
            version(grpcServerSpringBootAutoconfigureVersion, grpcServerSpringBootAutoconfigureLibVersion)
            library("grpc-server-autoconfigure", "net.devh", "grpc-server-spring-boot-autoconfigure").versionRef(grpcServerSpringBootAutoconfigureVersion)
            // grpc-spring-boot
            version(grpcSpringBootVersion, grpcSpringBootLibVersion)
            library("grpc-spring-boot", "io.github.lognet", "grpc-spring-boot-starter").versionRef(grpcSpringBootVersion)
            // jakarta
            version(jakartaAnnotationVersion, jakartaAnnotationLibVersion)
            library("jakarta-annotation-api", "jakarta.annotation", "jakarta.annotation-api").versionRef(jakartaAnnotationVersion)
            // jetbrains
            version(jetbrainsKotlinVersion, kotlinVersion)
            library("kotlin-bom", "org.jetbrains.kotlin", "kotlin-bom").versionRef(jetbrainsKotlinVersion)
            library("kotlin-stdlib-common", "org.jetbrains.kotlin", "kotlin-stdlib-common").versionRef(jetbrainsKotlinVersion)
            library("kotlin-stdlib-jdk8", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8").versionRef(jetbrainsKotlinVersion)
            library("kotlin-gradle-plugin", "org.jetbrains.kotlin", "kotlin-gradle-plugin").versionRef(jetbrainsKotlinVersion)
            library("kotlin-script-runtime", "org.jetbrains.kotlin", "kotlin-script-runtime").versionRef(jetbrainsKotlinVersion)
            library("kotlin-plugin-noarg", "org.jetbrains.kotlin", "kotlin-noarg").versionRef(jetbrainsKotlinVersion)
            // kotlin-reflect
            version(jetbrainsKotlinReflectVersion, kotlinVersion)
            library("kotlin-reflect","org.jetbrains.kotlin","kotlin-reflect").versionRef(jetbrainsKotlinReflectVersion)
            // kotlin-noarg
            version(jetbrainsKotlinNoArgVersion, kotlinVersion)
            library("kotlin-noarg", "org.jetbrains.kotlin", "kotlin-noarg").versionRef(jetbrainsKotlinNoArgVersion)
            // serialization
            version(kotlinSerializationVersion, kotlinSerializationLibVersion)
            library("kotlin-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef(kotlinSerializationVersion)
            // minio
            version(minioGroupVersion, minioGroupLibVersion)
            library("minio", "io.minio", "minio").versionRef(minioGroupVersion)
            // corutines
            version(kotlinxVersion, kotlinxLibVersion)
            library("coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef(kotlinxVersion)
            library("coroutines-core-jvm", "org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm").versionRef(kotlinxVersion)
            // lincheck
            version(kotlinxLincheckVersion, kotlinxLincheckLibVersion)
            library("lincheck", "org.jetbrains.kotlinx", "lincheck").versionRef(kotlinxLincheckVersion)
            // corutines-reactive
            library("coroutines-reactor", "org.jetbrains.kotlinx", "kotlinx-coroutines-reactor").versionRef(kotlinxVersion)
            library("coroutines-reactive", "org.jetbrains.kotlinx", "kotlinx-coroutines-reactive").versionRef(kotlinxVersion)
            library("coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef(kotlinxVersion)
            // log4j2
            version(log4jApiVersion, log4jApiLibVersion)
            library("log4j-bom", "org.apache.logging.log4j", "log4j-bom").versionRef(log4jApiVersion)
            library("log4j-api", "org.apache.logging.log4j", "log4j-api").versionRef(log4jApiVersion)
            library("log4j-core", "org.apache.logging.log4j", "log4j-core").versionRef(log4jApiVersion)
            library("log4j-jcl", "org.apache.logging.log4j", "log4j-jcl").versionRef(log4jApiVersion)
            library("log4j-slf4j", "org.apache.logging.log4j", "log4j-slf4j-impl").versionRef(log4jApiVersion)
            library("log4j-jakarta-smtp", "org.apache.logging.log4j", "log4j-jakarta-smtp").versionRef(log4jApiVersion)
            // log4j2 4 kotlin
            version(log4jApiKotlinVersion, log4jApiKotlinLibVersion)
            library("log4j-api-kotlin", "org.apache.logging.log4j", "log4j-api-kotlin").versionRef(log4jApiKotlinVersion)
            // bucket4j
            version(bucket4jVersion, bucket4jLibVersion)
            library("bucket4j-core", "com.bucket4j", "bucket4j-core").versionRef(bucket4jVersion)
            // jsoup
            version(jsoupVersion, jsoupLibVersion)
            library("jsoup", "org.jsoup", "jsoup").versionRef(jsoupVersion)
            // detekt
            version(detektPluginVersion, detektPluginLibVersion)
            library("detekt-formatting", "io.gitlab.arturbosch.detekt", "detekt-formatting").versionRef(detektPluginVersion)
            library("detekt-rules-libraries", "io.gitlab.arturbosch.detekt", "detekt-rules-libraries").versionRef(detektPluginVersion)
            library("detekt-rules-ruleauthors", "io.gitlab.arturbosch.detekt", "detekt-rules-ruleauthors").versionRef(detektPluginVersion)
            // google firebase
            version(googleFirebaseVersion, googleFirebaseLibVersion)
            library("google-firebase-admin", "com.google.firebase", "firebase-admin").versionRef(googleFirebaseVersion)
            // apple pushy
            version(applePushyVersion, applePushyLibVersion)
            library("apple-pushy", "com.eatthepath", "pushy").versionRef(applePushyVersion)
            // device detector
            version(deviceDetectorVersion, deviceDetectorLibVersion)
            library("devicedetector", "io.github.mngsk", "device-detector").versionRef(deviceDetectorVersion)
            // stripe payments
            version(stripePaymentsVersion, stripePaymentsLibVersion)
            library("stripe-java", "com.stripe", "stripe-java").versionRef(stripePaymentsVersion)
            //google
            version(googleGsonVersion, googleGsonLibVersion)
            library("google.gson", "com.google.code.gson", "gson").versionRef(googleGsonVersion)
            // neatchess
            version(neatChessVersion, neatChessLibVersion)
            library("neatchess", "net.andreinc", "neatchess").versionRef(neatChessVersion)
        }
    }
}
