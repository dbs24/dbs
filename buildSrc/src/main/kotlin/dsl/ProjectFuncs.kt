package dsl

import dsl.Dependencies.Core.EMPTY_STRING
import org.gradle.api.Project

object ProjectFuncs {
    fun Project.findArtifact(keyLibProperty: String, propertyName: String, extLib: String? = null) = let {
        "$keyLibProperty:${this.properties[propertyName]}${extLib?.let { ":$it" } ?: EMPTY_STRING}"
    }
}
