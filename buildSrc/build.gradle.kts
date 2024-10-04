import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0

plugins {
    `version-catalog`
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

kotlin {
    compilerOptions.apply {
        languageVersion.set(KOTLIN_2_0)
        apiVersion.set(KOTLIN_2_0)
    }

    sourceSets.all {
        languageSettings {
            languageVersion = KOTLIN_2_0.version
            apiVersion = KOTLIN_2_0.version
            //progressiveMode = true
        }
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}
