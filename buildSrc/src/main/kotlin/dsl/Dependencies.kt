package dsl


object Dependencies {

    object Core {
        const val PROJECT_GROUP = "org.dbs"
        const val PRODUCT_DESCRIPTION = "dbs24"
        const val PRODUCT_VERSION = "0.0.1"
        const val ROOT_PROJECT = "ROOT PROJECT"
        const val KOTLIN_LANG_VERSION = "2.0"
        const val JVM_VERSION = "21"
        const val EMPTY_STRING = ""
        const val STDLIB = "stdlib"
    }

    object Spring {
        const val springBootGroup = "org.springframework.boot"
        const val springBootDataGroup = "org.springframework.data"
        const val springBootCloudGroup = "org.springframework.cloud"
        const val springBootKafkaGroup = "org.springframework.kafka"
        const val springDocGroup = "org.springdoc"
        const val jetbrainsGroup = "org.jetbrains.kotlin"
        const val mongoDbGroup = "org.mongodb"
        const val netDevh = "net.devh"
        const val ioGrpc = "io.grpc"
        const val springFasterXmlGroup = "com.fasterxml.jackson.core"
        const val projectReactorGroup = "io.projectreactor"
        const val jsonWebTokenGroup = "io.jsonwebtoken"
        const val githubLogNet = "io.github.lognet"
        const val apacheLoggingGroup = "org.apache.logging.log4j"
        const val orgYmlGroup = "org.yaml"
        const val checkstyleGroup = "com.puppycrawl.tools"
        const val jacocoGroup = "org.jacoco"

        // exludes
        const val springExcStarter = "spring-boot-starter"
        const val springExcStarterLogging = "spring-boot-starter-logging"
        const val springExcApacheLogging = "log4j-to-slf4j"
    }

    object ApplicationAttributes {
        const val MAIN_CLASS = "Main-Class"
    }

    object ProtoBuffSettings {
        const val GRPC = "grpc"
        const val GRPC_KT = "grpckt"
        private const val GOOGLE_PROTOBUF = "com.google.protobuf"
        const val PROTOC_LIB = "$GOOGLE_PROTOBUF:protoc"
        const val PROTOC_LIB_VAR = "googleProtobufLibVersion"
        const val GRPC_JAVA_LIB = "io.grpc:protoc-gen-grpc-java"
        const val GRPC_JAVA_LIB_VAR = "grpcLibVersion"
        const val GRPC_KT_LIB = "io.grpc:protoc-gen-grpc-kotlin"
        const val GRPC_KT_LIB_VAR = "grpcKotlinStubLibVersion"
        const val JDK8_JAR = "jdk8@jar"
    }
    object ProjectAttributes {
        const val MAIN_CLASS_NAME = "mainClassName"
        const val IMPLEMENTATION_TITLE = "Implementation-Title"
        const val IMPLEMENTATION_VERSION = "Implementation-Version"
        const val NO_VERSION = ""
    }

    object Projects {
        const val STD_LIB = "stdlib"
        private const val SSS = "3s"
        private const val API = "api"
        private const val CLIENT = "client"
        private const val PROTO = "proto"
        private const val GRPC = "grpc"
        private const val SHARED = "shared"
        private const val SHARED_2 = ":$SHARED:"
        const val APPLICATION_CORE_API = SHARED_2 + "application-core-$API"
        const val CACHE_STARTER = SHARED_2 + "cache-starter"
        const val ENTITY_CORE_API = SHARED_2 + "entity-core-$API"
        const val GRPC_API = SHARED_2 + "spring-grpc-api"
        const val GRPC_SERVER_STARTER = SHARED_2 + "spring-grpc-server-starter"
        const val GRPC_CLIENT = SHARED_2 + "spring-grpc-client-starter"
        const val GRPC_HTTP_API = SHARED_2 + "grpc-http-$API"
        const val KAFKA_API = SHARED_2 + "kafka-$API"
        const val MAIL_STARTER = SHARED_2 + "mail-starter"
        const val MEDIA_STARTER = SHARED_2 + "media-starter"
        const val MONGO_STARTER = SHARED_2 + "mongo-starter"
        const val NO_VERSION_ASSIGNED = ""
        const val PERSISTENCE_API = SHARED_2 + "persistence-$API"
        const val R2DBC_API = SHARED_2 + "r2dbc-$API"
        const val R2DBC_STARTER = SHARED_2 + "r2dbc-starter"
        const val REF_STARTER = SHARED_2 + "ref-starter"
        const val SCHOOL_CACHE_STARTER = SHARED_2 + "smart-safe-school-cache-starter"
        const val SCHOOL_MEDIA_API = SHARED_2 + "smart-safe-school-media-$API"
        const val SECURITY_CONFIG_STARTER = SHARED_2 + "security-config-starter"
        const val SECURITY_MANAGER_API = SHARED_2 + "security-manager-$API"
        const val SPRING_BOOT_API = SHARED_2 + "spring-boot-$API"
        const val SPRING_CONFIG = SHARED_2 + "spring-config"
        const val SPRING_CORE_API = SHARED_2 + "spring-core-$API"
        const val SPRING_KAFKA_STARTER = SHARED_2 + "spring-kafka-starter"
        const val SPRING_REST_API = SHARED_2 + "spring-rest-$API"
        const val TEST_API = SHARED_2 + "test-$API"
        const val TEST_CORE = SHARED_2 + "test-core"
        const val TEST_JUNIT_API = SHARED_2 + "test-junit-$API"

        // auth-server-client
        private const val AUTH = "auth"
        private const val AUTH_FLD = ":$AUTH"
        private const val AUTH_SERVER = "$AUTH-server"
        private const val AUTH_VERIFY = "$AUTH-verify"
        const val AUTH_SERVER_API = "$AUTH_FLD:$AUTH_SERVER-$API"
        const val AUTH_SERVER_PROTO_API = "$AUTH_FLD:$AUTH-$PROTO-$API"
        const val AUTH_SERVER_CLIENT_API = "$AUTH_FLD:$AUTH-$PROTO-$CLIENT"

        // cc
        const val CC_MGMT = "mgmt"
        private const val CC_MGMT_FLD = ":cc"
        const val CC_MGMT_GRPC_CLIENT = "$CC_MGMT_FLD:$CC_MGMT-grpc-client"
        const val CC_MGMT_PROTO_API = "$CC_MGMT_FLD:$CC_MGMT-proto-api"
        const val CC_MGMT_API = "$CC_MGMT_FLD:$CC_MGMT-api"

        // goods
        const val CC_GOODS = "goods"
        private const val CC_GOODS_FLD = ":industrial"
        const val CC_GOODS_GRPC_CLIENT = "$CC_GOODS_FLD:$CC_GOODS-grpc-client"
        const val CC_GOODS_PROTO_API = "$CC_GOODS_FLD:$CC_GOODS-proto-api"
        const val CC_GOODS_API = "$CC_GOODS_FLD:$CC_GOODS-api"

        // tt
        const val TIK_CORE = "core"
        private const val TT_MGMT_FLD = ":tik"
        const val TT_MGMT_GRPC_CLIENT = "$TT_MGMT_FLD:$TIK_CORE-grpc-client"
        const val TT_MGMT_PROTO_API = "$TT_MGMT_FLD:$TIK_CORE-proto-api"
        const val TT_MGMT_API = "$TT_MGMT_FLD:$TIK_CORE-api"

        // sandbox
        const val CC_SANDBOX = "sandbox"
        private const val CC_SANDBOX_FLD = ":cc"
        const val CC_SANDBOX_GRPC_CLIENT = "$CC_SANDBOX_FLD:$CC_SANDBOX-grpc-client"
        const val CC_SANDBOX_PROTO_API = "$CC_SANDBOX_FLD:$CC_SANDBOX-proto-api"
        const val CC_SANDBOX_API = "$CC_SANDBOX_FLD:$CC_SANDBOX-api"

        // proto
        private const val PROTOBUF_CORE = "protobuf-core"
        const val PROTOBUF_API = "protobuf-$API"
        const val PROTOBUF_API_SRC = "$SHARED_2$PROTOBUF_CORE:$PROTOBUF_API"
        // cm-analyst
        const val CM_ANALYST = "analyst"
        private const val CM_ANALYST_FLD = ":p-cm"
        const val CM_ANALYST_GRPC_CLIENT = "$CM_ANALYST_FLD:$CM_ANALYST-grpc-client"
        const val CM_ANALYST_PROTO_API = "$CM_ANALYST_FLD:$CM_ANALYST-proto-api"
        const val CM_ANALYST_API = "$CM_ANALYST_FLD:$CM_ANALYST-api"
        const val CM_TASKER = "$CM_ANALYST_FLD:tasker"

        // out-of-service-client
        private const val OUT_OF_SERVICE = "out-of-service"
        private const val OUT_OF_SERVICE_FLD = ":$OUT_OF_SERVICE"
        const val OUT_OF_SERVICE_API = "$OUT_OF_SERVICE_FLD:$OUT_OF_SERVICE-$API"
        const val OUT_OF_SERVICE_PROTO_API = "$OUT_OF_SERVICE_FLD:$OUT_OF_SERVICE-$PROTO-$API"

    }
}
