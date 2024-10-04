plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

group = "org.dbs"
description = "dbs24"
version = "0.0.1"

//java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications {
//        maven(MavenPublication) {
//            from(components.java)
//        }
    }
}
