import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2

plugins {
    `version-catalog`
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(KOTLIN_2_2.version))
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(KOTLIN_2_2.version))
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}
