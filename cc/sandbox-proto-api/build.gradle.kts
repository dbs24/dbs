import com.google.protobuf.gradle.id
import dsl.Dependencies.Projects.PROTOBUF_API_SRC
import dsl.Dependencies.ProtoBuffSettings.GRPC
import dsl.Dependencies.ProtoBuffSettings.GRPC_JAVA_LIB
import dsl.Dependencies.ProtoBuffSettings.GRPC_JAVA_LIB_VAR
import dsl.Dependencies.ProtoBuffSettings.GRPC_KT
import dsl.Dependencies.ProtoBuffSettings.GRPC_KT_LIB
import dsl.Dependencies.ProtoBuffSettings.GRPC_KT_LIB_VAR
import dsl.Dependencies.ProtoBuffSettings.JDK8_JAR
import dsl.Dependencies.ProtoBuffSettings.PROTOC_LIB
import dsl.Dependencies.ProtoBuffSettings.PROTOC_LIB_VAR
import dsl.ProjectFuncs.findArtifact

plugins {
    idea
    id("com.google.protobuf")
}

dependencies {

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)
    implementation(libs.coroutines.reactor)
    implementation(libs.grpc.all)
    implementation(libs.grpc.protoc)
    implementation(libs.grpc.protoc.kotlin)
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.gen)
    implementation(platform(libs.protobuf.bom))
    protobuf(project(PROTOBUF_API_SRC))
}

description = "mgmt-proto-api"

protobuf {
    protoc { artifact = findArtifact(PROTOC_LIB, PROTOC_LIB_VAR) }
    plugins {
        id(GRPC) { artifact = findArtifact(GRPC_JAVA_LIB, GRPC_JAVA_LIB_VAR) }
        id(GRPC_KT) { artifact = findArtifact(GRPC_KT_LIB, GRPC_KT_LIB_VAR, JDK8_JAR) }
    }
    generateProtoTasks {
        all().forEach {
            if (it.name.startsWith("generateTestProto")) {
                it.dependsOn("jar")
            }
            it.plugins {
                id(GRPC)
                id(GRPC_KT)
            }
        }
    }
}
