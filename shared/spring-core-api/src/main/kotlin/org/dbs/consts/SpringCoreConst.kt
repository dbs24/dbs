package org.dbs.consts

import org.dbs.application.core.service.funcs.TestFuncs.generateTestString10
import org.dbs.consts.SpringCoreConst.PropertiesNames.BANNER_ROW_DELIMITER
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_BLACK_LIST_IPS
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_BLACK_LIST_IPS_DEF_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ONLY_ALLOWED_IPS
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ONLY_ALLOWED_IPS_DEF_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_TRUSTED_IPS
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_TRUSTED_IPS_DEF_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_USE_X_REAL_IP_HEADER
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_SECURITY_PROFILE_ADVANCED_WHITE_URI_LIST
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_SERVER_PORT
import org.dbs.consts.SpringCoreConst.PropertiesNames.CUSTOM_BANNER_SET_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.GOOGLE_SERVER
import org.dbs.consts.SpringCoreConst.PropertiesNames.GOOGLE_SERVER_RECAPTCHA_V3_ROUTES_SITE_VERIFY
import org.dbs.consts.SpringCoreConst.PropertiesNames.GOOGLE_SERVER_RECAPTCHA_V3_SECRET
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_CC_PLAYER_ACCESS_EXPIRATION_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_CC_PLAYER_REFRESH_EXPIRATION_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.KAFKA_BOOSTRAP_SERVERS
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAIL_FROM_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAIL_HOST_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAIL_PORT_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAIL_PROTOCOL_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAIL_USER_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.MINIO_ACCESS_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.MINIO_HOST_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.MINIO_SECRET_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.NOT_AVAILABLE
import org.dbs.consts.SpringCoreConst.PropertiesNames.REFERENCES_AUTO_SYNCHRONIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_ABUSED_QP_LIST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HEADERS
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HEADERS_BREAK
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HEADERS_YML
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HOSTS
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HOSTS_BREAK
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HOSTS_YML
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_KEY_STORE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_KEY_STORE_TYPE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_JMX_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_MAIL_ASYNC_SEND_ALG
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_MAIL_PROCESS_BATCH_INTERVAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_MAIL_PROCESS_BATCH_LIMIT
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_MAX_LIFE_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_POOL_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_POOL_IS
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_POOL_MS
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_URL
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_REDIS_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_REDIS_PORT
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_EVICT_IN_BACKGROUND
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_CONNECTIONS
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_IDLE_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_LIFE_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_TEST_CONNECTION
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.MASK_ALL
import org.dbs.consts.SysConst.STRING_FALSE
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.Part
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Consumer

typealias NoArg2Mono<T> = () -> Mono<T>
typealias String2Mono = (String) -> Mono<*>
typealias SuspendNoArg2Mono<T> = suspend () -> Mono<T>
typealias NoArg2Flux<T> = () -> Flux<T>
typealias Arg2Mono<K, V> = (K) -> Mono<V>
typealias SuspendArg2Mono<K, V> = suspend (K) -> Mono<V>
typealias Args2Mono<K1, K2, V> = (K1, K2) -> Mono<V>
typealias Mono2Mono<K, V> = (Mono<K>) -> Mono<V>
typealias RequestVersion = String
typealias WebClientUriBuilder = Arg2Generic<UriBuilder, UriBuilder>
typealias MultipartMap = MultiValueMap<String, Part>

object SpringCoreConst {

    object Beans {
        const val ENTITY_CORE_ACTION_SERVICE = "entityCoreActionService"
        const val USERS_SERVICE = "usersService"
        const val ROLES_SERVICE = "rolesService"
        const val DEFAULT_PROXY_BEANS_VAL = false
    }

    object App {
        const val BUFFER_APP_SIZE = 8092
        const val SUCCESSFULLY_STARTED = """
╔═╗┬ ┬┌─┐┌─┐┌─┐┌─┐┌─┐┌─┐┬ ┬┬  ┬ ┬ ┬  ┌─┐┌┬┐┌─┐┬─┐┌┬┐┌─┐┌┬┐
╚═╗│ ││  │  ├┤ └─┐└─┐├┤ │ ││  │ └┬┘  └─┐ │ ├─┤├┬┘ │ ├┤  ││
╚═╝└─┘└─┘└─┘└─┘└─┘└─┘└  └─┘┴─┘┴─┘┴   └─┘ ┴ ┴ ┴┴└─ ┴ └─┘─┴┘                                                                                                                                                                                                                                           
        """
        const val STOPPED = """
 @@@@@@   @@@@@@@   @@@@@@   @@@@@@@   @@@@@@@   @@@@@@@@  @@@@@@@   
@@@@@@@   @@@@@@@  @@@@@@@@  @@@@@@@@  @@@@@@@@  @@@@@@@@  @@@@@@@@  
!@@         @@!    @@!  @@@  @@!  @@@  @@!  @@@  @@!       @@!  @@@  
!@!         !@!    !@!  @!@  !@!  @!@  !@!  @!@  !@!       !@!  @!@  
!!@@!!      @!!    @!@  !@!  @!@@!@!   @!@@!@!   @!!!:!    @!@  !@!  
 !!@!!!     !!!    !@!  !!!  !!@!!!    !!@!!!    !!!!!:    !@!  !!!  
     !:!    !!:    !!:  !!!  !!:       !!:       !!:       !!:  !!!  
    !:!     :!:    :!:  !:!  :!:       :!:       :!:       :!:  !:!  
:::: ::      ::    ::::: ::   ::        ::        :: ::::   :::: ::  
:: : :       :      : :  :    :         :        : :: ::   :: :  :   
                                                                                
        """
    }


    object PropertiesNames {
        // Generic
        const val NOT_AVAILABLE = "n/a"
        const val DEV_MODE = "server.devMode"
        const val JUNIT_MODE = "server.junitMode"
        const val KOTEST_MODE = "server.kotest.mode"
        // SERVER
        const val SERVER_SESSION_TIME_OUT = "server.servlet.session.timeout"
        // SPRING BOOT
        //--------------------------------------------------------------------------------------------------------------
        const val SPRING_BOOT_VERSION = "spring-boot.version"
        const val SPRING_JMX_ENABLED = "spring.jmx.enabled"
        const val SPRING_BOOT_LAZY_INIT = "spring.main.lazy-initialization"
        const val SPRING_BOOT_WEB_APP_TYPE = "spring.main.web-application-type"
        const val SPRING_ACTIVE_PROFILE = "spring.profiles.active"
        const val REACTIVE_APPLICATION = "reactive"
        const val SPRING_THREADS_VIRTUAL = "spring.threads.virtual.enabled:false"

        //--------------------------------------------------------------------------------------------------------------
        const val CONFIG_REMOTE_SHUTDOWN_ALLOWED = "remote.Shutdown.allowed"
        const val VALUE_REMOTE_SHUTDOWN_ALLOWED = "true"

        //--------------------------------------------------------------------------------------------------------------
        // APPLICATION
        //--------------------------------------------------------------------------------------------------------------
        const val SPRING_APPLICATION_NAME = "spring.application.name"
        const val SPRING_APPLICATION_HOST = "spring.application.host"
        const val USER_TIME_ZONE = "user.timezone"

        //--------------------------------------------------------------------------------------------------------------
        // SECURITY
        //--------------------------------------------------------------------------------------------------------------
        const val SECURITY_WHITE_HEADERS = "White Headers List"
        const val SECURITY_WHITE_HEADERS_YML = "config.security.h1.white-headers"
        const val SECURITY_WHITE_HEADERS_BREAK = "config.security.h1.break-illegal-header"
        const val SECURITY_WHITE_HOSTS = "White Hosts List"
        const val SECURITY_WHITE_HOSTS_YML = "config.security.h1.white-hosts"
        const val SECURITY_ABUSED_QP_LIST = "config.security.h1.abused.query-params-values"
        const val SECURITY_WHITE_HOSTS_BREAK = "config.security.h1.break-illegal-host"
        const val SECURITY_WHITE_HEADERS_LIST = "X-Real-IP, X-Forwarded-For"

        //const val SECURITY_MANDATORY_HEADERS_LIST = "User-Agent, Host"
        const val BROWSER_HEADERS_LIST = ", Content-Type, Content-Length, Accept, Accept-Language, Accept-Encoding, " +
                "Connection, Upgrade-Insecure-Requests, Sec-Fetch-Dest, Sec-Fetch-Mode, Sec-Fetch-Site, Sec-Fetch-User," +
                "sec-ch-ua, sec-ch-ua-mobile, sec-ch-ua-platform, Pragma, Cache-Control, Cookie, User-Agent, Host, " +
                "Authorization, Origin, Referer, X-Request-ID, X-Forwarded-Host, X-Forwarded-Proto, X-Forwarded-Scheme, " +
                "X-Forwarded-Port, X-Original-Forwarded-For, X-Scheme"

        //--------------------------------------------------------------------------------------------------------------
        // CORE
        //--------------------------------------------------------------------------------------------------------------
        const val CONFIG_SERVER_PORT = "server.port"
        const val CONFIG_SERVER_PORT_DEF = "9999"
        const val CONFIG_SERVER_EVENT_LOOP_GROUPS = "server.event-loop-groups"
        const val SERVER_SSL_ENABLED = "server.ssl.enabled"
        const val SERVER_SSL_KEY_STORE = "server.ssl.key-store"
        const val SERVER_SSL_KEY_STORE_TYPE = "server.ssl.key-store-type"
        const val SERVER_SSL_DISABLED = "false"
        const val SERVER_SSL_ENABLED_VALUE = "true"

        //--------------------------------------------------------------------------------------------------------------
        // DATABASE
        //--------------------------------------------------------------------------------------------------------------
        const val SPRING_R2DBC_URL = "spring.r2dbc.url"
        const val SPRING_R2DBC_POOL_ENABLED = "spring.r2dbc.pool.enabled"
        const val SPRING_R2DBC_POOL_IS = "spring.r2dbc.pool.initial-size"
        const val SPRING_R2DBC_POOL_MS = "spring.r2dbc.pool.max-size"
        const val SPRING_R2DBC_USER_NAME = "spring.r2dbc.username"
        const val SPRING_R2DBC_PASSWORD = "spring.r2dbc.password"
        const val SPRING_R2DBC_NEXT_VAL_CMD = "spring.r2dbc.next-val-cmd"
        const val NEXT_VAL_ACTION = "nextval('seq_action_id')"
        const val SPRING_R2DBC_MAX_LIFE_TIME = "spring.r2dbc.pool.max-life-time"
        const val SELECT_NEXT_VAL_ACTION = "SELECT $NEXT_VAL_ACTION"
        const val SEQ_NAME_TEMPLATE = "SEQ_NAME"
        const val SELECT_NEXT_VAL_GENERIC = "SELECT nextval('$SEQ_NAME_TEMPLATE')"

        // GRPC
        const val GRPC_SERVER_PORT = "grpc.server.port"
        const val GRPC_SERVER_SECURITY = "grpc.server.security.enabled"
        const val GRPC_SERVER_SSL_DISABLED = "false"
        const val GRPC_SERVER_CERT = "grpc.server.security.certificateChain"
        const val GRPC_SERVER_CERT_KEY = "grpc.server.security.privateKey"
        const val GRPC_SERVER_HEALTH_SERVICE = "grpc.server.health-service-enabled"

        // MONGO
        const val SPRING_DATA_MONGODB_URI = "spring.data.mongodb.uri"

        // REDIS
        const val SPRING_REDIS_HOST = "spring.data.redis.host"
        const val SPRING_REDIS_PORT = "spring.data.redis.port"
        const val SPRING_REDIS_PWD = "spring.data.redis.password"
        const val CONFIG_SPRING_REDIS_INVALIDATE = "spring.data.redis.invalidate"
        const val SPRING_REDIS_INVALIDATE_HOURS = "8"

        // LOGGING
        const val LOGGING_LEVEL_ROOT = "logging.level.root"

        //--------------------------------------------------------------------------------------------------------------
        // WEB
        const val SPRING_WEB_LOCALE = "spring.web.locale"
        const val SPRING_WEB_LOCALE_DEF_VAL = "en_EN"
        const val CONFIG_SPRING_WEBSOCKET_MAPPING_PATH = "spring.websocket.mapping-path"
        const val VALUE_SPRING_WEBSOCKET_MAPPING_PATH = "/websocket"

        // EMAIL
        const val SPRING_MAIL_FROM = "spring.mail.from"
        const val SPRING_MAIL_RETRY_ATTEMPT_LIMIT = "spring.mail.retry-attempt-limit"
        const val SPRING_MAIL_PROCESS_BATCH_LIMIT = "spring.mail.process-batch-limit"
        const val SPRING_MAIL_ASYNC_SEND_ALG = "spring.mail.async-send-alg"
        const val SPRING_MAIL_PROCESS_BATCH_INTERVAL = "spring.mail.process-batch-interval-seconds"
        const val SPRING_MAIL_DELAY_RESEND = "spring.mail.process-batch-limit.resend.delay"

        //--------------------------------------------------------------------------------------------------------------
        // DEV-TOOLS
        //--------------------------------------------------------------------------------------------------------------
        const val SPRING_DEVTOOLS_RESTART_ENABLED = "spring.devtools.restart.enabled"

        //--------------------------------------------------------------------------------------------------------------
        // KAFKA
        //--------------------------------------------------------------------------------------------------------------
        const val KAFKA_ENABLED = "spring.kafka.enabled"
        const val KAFKA_BOOSTRAP_SERVERS = "spring.kafka.bootstrap-servers"
        const val KAFKA_PROCESSING_INTERVAL = "config.kafka.processing-interval"
        const val KAFKA_PROCESSING_INTERVAL_DEF = "5000"
        const val KAFKA_PROCESSING_CRON = "config.kafka.processing-cron"

        //--------------------------------------------------------------------------------------------------------------
        // CUSTOM
        //--------------------------------------------------------------------------------------------------------------
        const val CUSTOM_BANNER_SET_KEY = "custom.banner.set.key"
        const val YML_REST_ROUTES_ENABLED = "spring.rest.routes.enabled"
        const val YML_CORS_CONFIG_ENABLED = "network.cors.config.enabled"

        // OTHER
        //--------------------------------------------------------------------------------------------------------------
        const val DEFAULT_SYS_CURRENCY = "config.restful.security.smart-safe-school-store.currency.store-currency"
        const val DEFAULT_SYS_CURRENCY_VALUE = "USD"
        const val ASYNC_ALG_STORE_ENABLED = "config.entity.core.action.execution.async-alg-store.enabled"
        const val ASYNC_ALG_STORE_ENABLED_VALUE = "false"
        const val ASYNC_ALG_STORE_DELAY = "config.entity.core.action.execution.async-alg-store.delay"
        const val ASYNC_ALG_STORE_DELAY_VALUE = 50L

        //--------------------------------------------------------------------------------------------------------------
        const val QUERY_MAX_EXEC_TIME = "config.restful.query.max-time-exec"
        const val QUERY_MAX_EXEC_TIME_VALUE = 500
        const val TEST_SOCKET_CONNECTION_LIMIT = 5

        //--------------------------------------------------------------------------------------------------------------
        const val WEB_CLIENT_MAX_CONNECTIONS = "config.webclient.maxConnections"
        const val WEB_CLIENT_MAX_CONNECTIONS_DEF = 500
        const val WEB_CLIENT_MAX_IDLE_TIME = "config.webclient.maxIdleTime"
        const val WEB_CLIENT_MAX_IDLE_TIME_DEF = 20
        const val WEB_CLIENT_MAX_LIFE_TIME = "config.webclient.maxLifeTime"
        const val WEB_CLIENT_MAX_LIFE_TIME_DEF = 60
        const val WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT = "config.webclient.pendingAcquireTimeout"
        const val WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT_DEF = 60
        const val WEB_CLIENT_EVICT_IN_BACKGROUND = "config.webclient.evictInBackground"
        const val WEB_CLIENT_EVICT_IN_BACKGROUND_DEF = 120
        const val WEB_CLIENT_TEST_CONNECTION = "config.webclient.testConnection"
        const val WEB_CLIENT_TEST_CONNECTION_DEF = true
        // SWAGGER
        //--------------------------------------------------------------------------------------------------------------
        const val SD_API_DOCS_ENABLED = "springdoc.api-docs.enabled"
        const val SD_SWAGGER_UI_ENABLED = "springdoc.swagger-ui.enabled"

        const val WC_1 = 500
        const val WC_2 = 5

        //--------------------------------------------------------------------------------------------------------------
        const val GRPC_MAX_EXEC_TIME = "config.grpc.query.max-time-exec"
        const val GRPC_MAX_EXEC_TIME_VALUE = 100

        //--------------------------------------------------------------------------------------------------------------
        const val GRPC_ENABLED_REFLECTION = "config.grpc.reflection.enabled"

        //--------------------------------------------------------------------------------------------------------------
        const val MAX_PAGE_SIZE = "config.ui.max-page-size"
        const val MAX_PAGE_SIZE_VALUE = 100
        const val DEF_PAGE_SIZE = "config.ui.def-page-size"
        const val DEF_PAGE_SIZE_VALUE = 20
        const val MAX_PAGES = "config.ui.max-pages"
        const val MAX_PAGES_VALUE = 2147483647
        const val REFERENCES_AUTO_SYNCHRONIZE = "config.references.auto-synchronize"
        const val JWT_SECRET_KEY = "config.security.jwt.secret-key"
        const val JWT_STORE_SECRET_KEY = "config.restful.security.smart-safe-school-store.jwt.secret-key"
        const val JWT_STORE_PUT_IP = "config.restful.security.smart-safe-school-store.jwt.ip2jwt"
        const val STORE_SERVER_ADDRESS = "config.restful.security.smart-safe-school-store.server"
        const val JWT_STORE_USER_JWT_EXPIRATION_TIME =
            "config.restful.security.smart-safe-school-store.server.api-create-user.jwt.expiration-time"
        const val JWT_STORE_ACCESS_EXPIRATION_TIME =
            "config.restful.security.smart-safe-school-store.jwt.expiration-time"
        const val JWT_STORE_REFRESH_EXPIRATION_TIME =
            "config.restful.security.smart-safe-school-store.refresh-jwt.expiration-time"

        const val JWT_CC_SECRET_KEY = "config.restful.security.cc-mgmt.jwt.secret-key"
        const val JWT_CC_PLAYER_ACCESS_EXPIRATION_TIME =
            "config.restful.security.cc-mgmt.jwt.expiration-time"
        const val JWT_CC_PLAYER_REFRESH_EXPIRATION_TIME =
            "config.restful.security.cc-mgmt.refresh-jwt.expiration-time"
        const val JWT_CM_PUT_IP = "config.restful.security.cm.jwt.ip2jwt"
        const val JWT_CM_SECRET_KEY = "config.restful.security.cm.jwt.secret-key"
        const val JWT_CM_ACCESS_EXPIRATION_TIME = "config.restful.security.cm.jwt.expiration-time"
        const val JWT_CM_REFRESH_EXPIRATION_TIME = "config.restful.security.cm.refresh-jwt.expiration-time"
        const val JWT_CM_SERVICE_JWT_EXPIRATION_TIME = "config.restful.security.cm.service-jwt.expiration-time"

        const val JWT_SCHOOL_SECRET_KEY = "config.restful.security.smart-safe-school.jwt.secret-key"
        const val JWT_SECRET_KEY_VALUE = "ThisIsSecretForJWTHS512ThisIsSecretForJWTHS513"
        const val JWT_ACCESS_EXPIRATION_TIME = "config.restful.security.smart-safe-school.jwt.expiration-time"
        const val JWT_REFRESH_EXPIRATION_TIME = "config.restful.security.smart-safe-school.refresh-jwt.expiration-time"
        const val JWT_V1_SECRET_KEY = "config.restful.security.smart-safe-school-v1.jwt.secret-key"
        const val JWT_V1_ACCESS_EXPIRATION_TIME = "config.restful.security.smart-safe-school-v1.jwt.expiration-time"
        const val JWT_V1_REFRESH_EXPIRATION_TIME =
            "config.restful.security.smart-safe-school-v1.refresh-jwt.expiration-time"
        const val CONFIG_BACK_DOOR_JWT = "server.backDoorJwt"
        const val VALUE_BACK_DOOR_JWT = "5Ofy88avj6gJtaU"
        const val CONFIG_RESTFUL_SECURITY_SSS_JWT_IP2JWT = "config.restful.security.smart-safe-school-store.jwt.ip2jwt"
        const val VALUE_RESTFUL_SECURITY_SSS_JWT_IP2JWT = "false"
        const val CONFIG_RESTFUL_SECURITY_SSS_JWT_SECRET_KEY = "config.security.jwt.secret-key"
        const val VALUE_RESTFUL_SECURITY_SSS_JWT_SECRET_KEY = "====================================="
        const val SECURITY_MANAGER_PASSWORD_EXPIRY = "config.security.manager-passwords.expiry"
        const val SECURITY_MANAGER_PASSWORD_UNIQUE = "config.security.manager-passwords.unique"
        const val SECURITY_ROUTES_ENABLED = "config.security.security-routes.enabled"

        //--------------------------------------------------------------------------------------------------------------
        const val CONFIG_RESTFUL_MESSAGE_PRINT_ENTITY_ID = "config.restful.message.print-entity-id"
        const val VALUE_RESTFUL_MESSAGE_PRINT_ENTITY_ID = "false"
        const val CONFIG_RESTFUL_RETURN_ERROR_2_RESPONSE = "config.restful.return-error-2-response"
        const val VALUE_RESTFUL_RETURN_ERROR_2_RESPONSE = "false"
        const val CONFIG_RESTFUL_MAX_DURATION_OF_SEC = "config.restful.max-duration"
        const val VALUE_RESTFUL_MAX_DURATION_OF_SEC = "30"
        const val CONFIG_RESTFUL_SECURITY_SSS_SERVER = "config.restful.security.smart-safe-school.server"
        const val VALUE_RESTFUL_SECURITY_SSS_SERVER = "https://localhost"
        const val CONFIG_RESTFUL_SECURITY_PC_SERVER = "config.restful.security.product-catalog.server"
        const val VALUE_RESTFUL_SECURITY_PC_SERVER = "https://localhost"
        const val CONFIG_EUREKA_BALANCER_ENABLED = "eureka.balancer.enabled"
        const val VALUE_EUREKA_BALANCER_ENABLED = "false"
        const val CONFIG_EUREKA_BALANCER_FIXED_POINTS_LIST = "eureka.balancer.fixed-points-list"
        const val VALUE_EUREKA_BALANCER_FIXED_POINTS_LIST = "/api/liveness,/api/readiness"
        const val CONFIG_REACTOR_REST_DEBUG = "config.reactor.rest.debug"
        const val VALUE_REACTOR_REST_DEBUG = "true"
        const val CONFIG_SECURITY_PROFILE_ADVANCED_WHITE_URI_LIST = "config.security.profile.advanced-white-uri-list"
        const val VALUE_SECURITY_PROFILE_ADVANCED_WHITE_URI_LIST = "not defined"
        const val CONFIG_NOTIFICATION_APNS_P12_FILE_PATH = "config.notification.apns.p12.file-path"
        const val CONFIG_NOTIFICATION_APNS_P12_PASSWORD = "config.notification.apns.p12.password"
        const val CONFIG_NOTIFICATION_APNS_TOPIC = "config.notification.apns.topic"

        //--------------------------------------------------------------------------------------------------------------
        const val NETWORK_CORS_ALLOWED_PATH = "network.cors.allowed-path"
        const val NETWORK_CORS_ALLOWED_ADDITIONAL_PATH = "network.cors.allowed-origins.additional"
        const val VALUE_NETWORK_CORS_ALLOWED_ADDITIONAL_PATH = "http://localhost:4200,http://localhost:5173"
        const val NETWORK_CORS_ALLOWED_HEADERS = "network.cors.allowed-headers"
        const val NETWORK_CORS_ALLOWED_METHODS = "network.cors.allowed-methods"
        const val NETWORK_CORS_ALLOWED_CREDENTIALS = "network.cors.allowed-credentials"
        const val NETWORK_CORS_MAX_AGE = "network.cors.max-age"

        //--------------------------------------------------------------------------------------------------------------
        const val SPRINGDOC_API_DOCS_ENABLED = "springdoc.api-docs.enabled"
        const val SPRINGDOC_SWAGGER_ENABLED = "springdoc.swagger-ui.enabled"

        //--------------------------------------------------------------------------------------------------------------
        // BUCKET_4J
        //--------------------------------------------------------------------------------------------------------------
        const val BUCKET_4J_ENABLED = "config.security.bucket4j.enabled"
        const val BUCKET_4J_USE_X_REAL_IP_HEADER = "config.security.bucket4j.use-x-real-ip"
        const val BUCKET_4J_RATE_LIMIT_CAPACITY = "config.security.bucket4j.rateLimit.capacity"
        const val BUCKET_4J_RATE_LIMIT_CAPACITY_DEF_VAL = 50L
        const val BUCKET_4J_RATE_LIMIT_TOKENS = "config.security.bucket4j.rateLimit.tokens"
        const val BUCKET_4J_RATE_LIMIT_TOKENS_DEF_VAL = 20L
        const val BUCKET_4J_RATE_LIMIT_MINUTES = "config.security.bucket4j.rateLimit.minutes"
        const val BUCKET_4J_RATE_LIMIT_TMP_MINUTES = "config.security.bucket4j.rateLimit.tmp-minutes"
        const val BUCKET_4J_RATE_LIMIT_MINUTES_DEF_VAL = 1L
        const val BUCKET_4J_RATE_LIMIT_MINUTES_BLACK_LIST_DEF_VAL = 30L
        const val BUCKET_4J_TRUSTED_IPS = "config.security.bucket4j.trusted-ips"
        const val BUCKET_4J_BLACK_LIST_IPS = "config.security.bucket4j.black-list-ips"
        const val BUCKET_4J_ONLY_ALLOWED_IPS = "config.security.bucket4j.allowed-only-ips"
        const val BUCKET_4J_TRUSTED_IPS_DEF_VAL = "locahost"
        const val BUCKET_4J_BLACK_LIST_IPS_DEF_VAL = "locahost"
        const val BUCKET_4J_ONLY_ALLOWED_IPS_DEF_VAL = "all"
        //--------------------------------------------------------------------------------------------------------------
        // REF
        //--------------------------------------------------------------------------------------------------------------
        const val CONFIG_REF_AUTO_SYNCHRONIZE = "config.references.auto-synchronize"
        const val CONFIG_REF_SYNCH_PRIVILEGE_GROUP = "config.references.privilege-group"
        const val CONFIG_REF_SYNCH_PRIVILEGE = "config.references.privilege"
        const val CONFIG_REF_ACTUAL_PRIVILEGE_GROUP_ID = "config.references.privilege-groupId"


        //--------------------------------------------------------------------------------------------------------------
        const val CONFIG_LIVENESS = "config.liveness"
        const val CONFIG_SECURITY = "config.security"

        //--------------------------------------------------------------------------------------------------------------
        const val MINIO_HOST_KEY = "config.filestorage.host"
        const val MINIO_ACCESS_KEY = "config.filestorage.accessKey"
        const val MINIO_SECRET_KEY = "config.filestorage.secretKey"

        // region Mail
        const val MAIL_HOST_KEY = "spring.mail.host"
        const val MAIL_PORT_KEY = "spring.mail.port"
        const val MAIL_PROTOCOL_KEY = "spring.mail.protocol"
        const val MAIL_FROM_KEY = "spring.mail.from"
        const val MAIL_USER_KEY = "spring.mail.username"
        // endregion


        //--------------------------------------------------------------------------------------------------------------
        //--Order
        const val CONFIG_UI_ORDERS_LINK = "config.ui.to-orders-link"
        const val URL_UI_ORDERS_LINK = "https://store-dev.smartsafeschool.com/profile/orders"

        //--Application-dispute
        const val APPLICATION_DISPUTE_EXPIRED_TIME_IN_MINUTES =
            "config.store.application-dispute.expired-time-in-minutes"
        const val APPLICATION_DISPUTE_EXPIRED_TIME_IN_MINUTES_DEF_VAL = 20160L

        //--Review-product
        const val REVIEW_PRODUCT_EXPIRED_TIME_IN_MINUTES =
            "config.store.review-product.expired-time-in-minutes"
        const val REVIEW_PRODUCT_EXPIRED_TIME_IN_MINUTES_DEF_VAL = 1440L


        //--Moodle-client
        const val PATH_MOODLE_CLIENT_MAIN_SERVER = "lms.moodle.host"
        const val URL_MOODLE_CLIENT_MAIN_SERVER = "https://moodle.ubuntu4.smartsafeschool.com"
        const val PATH_MOODLE_CLIENT_AUTH_TOKEN = "lms.moodle.token"
        const val URL_MOODLE_CLIENT_AUTH_TOKEN = "36e225e60480cff4e444f93b90e81807"

        //--Product-catalog-client
        const val PATH_PRODUCT_FOR_WH = "config.restful.security.product-catalog.server.api-exists-product"
        const val URL_PRODUCT_FOR_WH = "/api/product/for/wh/v1/get"
        const val PATH_PRODUCT_CATALOG_MAIN_SERVER = "config.restful.security.product-catalog.server.host-port"
        const val URL_PRODUCT_CATALOG_MAIN_SERVER = "https://clouds-dev.smartsafeschool.com:5443"
        const val PATH_PRODUCT_CATALOG_PRODUCT_LIST = "config.restful.security.product-catalog.server.product-list"
        const val URL_PRODUCT_CATALOG_PRODUCT_LIST = "/api/product/v2/get"

        //--Warehouse-client
        const val PATH_WAREHOUSE_CLIENT_MAIN_SERVER = "config.restful.security.warehouse.server.host-port"
        const val URL_WAREHOUSE_CLIENT_MAIN_SERVER = "https://clouds-dev.smartsafeschool.com:9443"
        const val PATH_WAREHOUSE_CLIENT_WAREHOUSE_LIST = "config.restful.security.warehouse.server.rest-list"
        const val URL_WAREHOUSE_CLIENT_WAREHOUSE_LIST = "/api/rest/list/v1/get"
        const val PATH_WAREHOUSE_CLIENT_TRANSACTION_LIST =
            "config.restful.security.warehouse.server.api-transactions-list"
        const val URL_WAREHOUSE_CLIENT_TRANSACTION_LIST = "/api/default"
        const val PATH_WAREHOUSE_CLIENT_TRANSACTION_GET = "config.restful.security.warehouse.server.api-transaction-get"
        const val URL_WAREHOUSE_CLIENT_TRANSACTION_GET = "/api/default"
        const val PATH_WAREHOUSE_CLIENT_TRANSACTION_CREATE =
            "config.restful.security.warehouse.server.api-transaction-create"
        const val URL_WAREHOUSE_CLIENT_TRANSACTION_CREATE = "/api/product/transaction/v1/create"

        //--Warehouse
        const val CONFIG_WAREHOUSE_MIN_SEARCH_KEYWORD_LEN = "config.ui.warehouse.search.min-length-keyword"
        const val VALUE_WAREHOUSE_MIN_SEARCH_KEYWORD_LEN = "1"
        const val CONFIG_WAREHOUSE_MANY = "config.store.warehouse.many"
        const val VALUE_WAREHOUSE_MANY = "false"

        //--Registry-application
        const val CONFIG_REGISTRY_APPLICATION_MIN_SEARCH_KEYWORD_LEN =
            "config.ui.registry.application.search.min-length-keyword"
        const val VALUE_REGISTRY_APPLICATION_MIN_SEARCH_KEYWORD_LEN = "1"

        //--Post
        const val CONFIG_POST_MIN_SEARCH_KEYWORD_LEN = "config.ui.post.search.min-length-keyword"
        const val VALUE_POST_MIN_SEARCH_KEYWORD_LEN = "1"

        //--Office
        const val CONFIG_OFFICE_MIN_SEARCH_KEYWORD_LEN = "config.ui.office.search.min-length-keyword"
        const val VALUE_OFFICE_MIN_SEARCH_KEYWORD_LEN = "1"

        //--Manager
        const val CONFIG_MANAGER_MIN_SEARCH_KEYWORD_LEN = "config.ui.manager.search.min-length-keyword"
        const val VALUE_MANAGER_MIN_SEARCH_KEYWORD_LEN = "1"

        //--Department
        const val CONFIG_DEPARTMENT_MIN_SEARCH_KEYWORD_LEN = "config.ui.department.search.min-length-keyword"
        const val VALUE_DEPARTMENT_MIN_SEARCH_KEYWORD_LEN = "1"

        //--Store
        const val CONFIG_STORE_EMAIL = "config.store.email"
        const val VALUE_STORE_EMAIL = "test@mail.ru"
        const val CONFIG_STORE_PHONE = "config.store.phone"
        const val VALUE_STORE_PHONE = "+37529"
        const val CONFIG_STORE_VAT_AMOUNT_IN_PERCENTS = "config.store.vat-amount-in-percents"
        const val URL_STORE_VAT_AMOUNT_IN_PERCENTS = "20"

        //--Activation-token
        const val CONFIG_UI_HOST = "config.ui.host"
        const val URL_UI_HOST = "http://localhost"
        const val CONFIG_CLIENT_CONFIRM_EMAIL_POINT = "config.ui.confirm-email-point"
        const val VALUE_CLIENT_CONFIRM_EMAIL_POINT = "/confirm-email"
        const val CONFIG_CLIENT_RESET_PASSWORD_POINT = "config.ui.reset-password-point"
        const val VALUE_CLIENT_RESET_PASSWORD_POINT = "/reset-password"
        const val CONFIG_CLIENT_REGISTRATION_POINT = "config.ui.registration-point"
        const val VALUE_CLIENT_REGISTRATION_POINT = "/registration"
        const val CONFIG_STORE_ACTIVATION_TOKEN_EXPIRED_TIME_IN_MIN =
            "config.store.activation.token.expired-time-in-minutes"
        const val VALUE_STORE_ACTIVATION_TOKEN_EXPIRED_TIME_IN_MIN = "1440"
        const val CONFIG_STORE_ACTIVATION_TOKEN_RESEND_TIME_IN_SEC =
            "config.store.activation.token.resend-time-in-seconds"
        const val CONFIG_STORE_ACTIVATION_TOKEN_RESEND_TIME_IN_SEC_DEF = "30"

        //--------------------------------------------------------------------------------------------------------------
        // School V1
        const val SCHOOL_V1_HOST = "smartsafeschool.com"
        const val SCHOOL_V1_HOST_PORT = "config.restful.security.schoolV1.server.host-port"
        const val SCHOOL_V1_HOST_PORT_DEF_VAL = "https://dev-main-api.smartsafeschool.com"
        const val SCHOOL_V1_URI_API_PAYMENT_BILL = "config.restful.security.schoolV1.server.api-payment-bill"
        const val SCHOOL_V1_URI_API_PAYMENT_BILL_DEF_VAL = "/api/payment/bill/user"
        const val SCHOOL_V1_URI_API_PAYMENT_BILL_CREATE = "config.restful.security.schoolV1.server.api-payment-create"
        const val SCHOOL_V1_URI_API_PAYMENT_BILL_CREATE_DEF_VAL = "/api/payment/bill/balance/pay"
        const val SCHOOL_V1_URI_API_GET_USER = "config.restful.security.schoolV1.server.api-get-user"
        const val SCHOOL_V1_URI_API_CREATE_CUSTOMER = "config.restful.security.schoolV1.server.api-create-customer"

        // region Media
        const val MEDIA_HOST_PORT = "config.restful.security.media.server.host-port"
        const val MEDIA_HOST_PORT_DEF_VAL = "https://localhost:4443"
        const val MEDIA_URI_UPLOAD = "config.restful.security.media.server.api-upload"
        const val MEDIA_URI_UPLOAD_DEF_VAL = "/api/mediaFile/v2/upload"
        // endregion

        //--------------------------------------------------------------------------------------------------------------
        // GRPC VISA-SERVICE
        const val VISA_SERVICE_HOST = "config.grpc.visa-service.server.host"
        const val VISA_SERVICE_PORT = "config.grpc.visa-service.server.port"

        //--------------------------------------------------------------------------------------------------------------
        // GRPC STRIPE-SERVICE
        const val PMT_SERVICE_HOST = "config.grpc.pmt-service.server.host"
        const val PMT_SERVICE_PORT = "config.grpc.pmt-service.server.port"

        //--------------------------------------------------------------------------------------------------------------
        // GRPC PLAYER-SERVICE
        const val MGMT_SERVICE_HOST = "config.grpc.cc-mgmt.server.host"
        const val MGMT_SERVICE_PORT = "config.grpc.cc-mgmt.server.port"

        //--------------------------------------------------------------------------------------------------------------
        // GRPC PLAYER-SERVICE
        const val SANDBOX_SERVICE_HOST = "config.grpc.cc-sandbox.server.host"
        const val SANDBOX_SERVICE_PORT = "config.grpc.cc-sandbox.server.port"

        //--------------------------------------------------------------------------------------------------------------
        // GRPC OUT-OF-SERVICE-SERVICE
        const val OUT_OF_SERVICE_SERVICE_HOST = "config.grpc.out-of-service-service.server.host"
        const val OUT_OF_SERVICE_SERVICE_PORT = "config.grpc.out-of-service-service.server.port"

        //--------------------------------------------------------------------------------------------------------------
        // GRPC AUTH-SERVICE
        const val AUTH_SERVER_SERVICE_HOST = "config.grpc.auth.server.host"
        const val AUTH_SERVER_SERVICE_PORT = "config.grpc.auth.server.port"

        //--goolge-client
        const val GOOGLE_SERVER = "config.restful.security.google.server.host-port"
        const val GOOGLE_SERVER_RECAPTCHA_V3 = "config.restful.security.google.server.recaptcha.v3"
        const val GOOGLE_SERVER_RECAPTCHA_V3_SITE = "$GOOGLE_SERVER_RECAPTCHA_V3.site"
        const val GOOGLE_SERVER_RECAPTCHA_V3_SECRET = "$GOOGLE_SERVER_RECAPTCHA_V3.secret"
        const val GOOGLE_SERVER_RECAPTCHA_V3_ROUTES = "config.restful.security.google.server.recaptcha.v3.routes"
        const val GOOGLE_SERVER_RECAPTCHA_V3_ROUTES_SITE_VERIFY = "$GOOGLE_SERVER_RECAPTCHA_V3_ROUTES.siteverify"

        //--------------------------------------------------------------------------------------------------------------
        // OTHER
        const val BANNER_ROW_DELIMITER = "------------------------------------------------------------"
        const val BANNER_ROW_BOLD_DELIMITER = "##################################################################################"
    }

    val DELIMITER: String
        get () = BANNER_ROW_DELIMITER.plus(generateTestString10())

    object ServiceConsts {
        const val TEST_CONNECTION_DEFAULT_TIMEOUT = 500L
        const val TEST_CONNECTION_TIMEOUT_STEP = 1000L
    }

    object WebClientConsts {
        const val UNLIMITED_BUFFER = -1
        val EMPTY_STATUS_PROCESSOR: WebClientOnStatusProcessor = {}
    }

    val EMPTY_HTTP_HEADERS: Consumer<HttpHeaders> = Consumer { it.addAll(LinkedMultiValueMap(0)) }

    val WEB_SET = mapOf(
        "Server port" to CONFIG_SERVER_PORT,
        "SSL enabled" to SERVER_SSL_ENABLED,
        "White uri list" to CONFIG_SECURITY_PROFILE_ADVANCED_WHITE_URI_LIST,
        "Jmx enabled" to SPRING_JMX_ENABLED,
        "SSL key store" to "$SERVER_SSL_KEY_STORE:$NOT_AVAILABLE",
        "SSL key store type" to "$SERVER_SSL_KEY_STORE_TYPE:$NOT_AVAILABLE",
        SECURITY_WHITE_HEADERS to "$SECURITY_WHITE_HEADERS_YML:$MASK_ALL",
        "Break illegal header" to "$SECURITY_WHITE_HEADERS_BREAK:$STRING_FALSE",
        SECURITY_WHITE_HOSTS to "$SECURITY_WHITE_HOSTS_YML:$MASK_ALL",
        "Break hosts header" to "$SECURITY_WHITE_HOSTS_BREAK:$STRING_FALSE",
        "Abused query params values" to "$SECURITY_ABUSED_QP_LIST",
        DELIMITER to EMPTY_STRING,
        "Bucket4j filter enabled" to "$BUCKET_4J_ENABLED:$STRING_FALSE",
        "Bucket4j trusted ips" to "$BUCKET_4J_TRUSTED_IPS:$BUCKET_4J_TRUSTED_IPS_DEF_VAL",
        "Bucket4j only allowed ips" to "$BUCKET_4J_ONLY_ALLOWED_IPS:$BUCKET_4J_ONLY_ALLOWED_IPS_DEF_VAL",
        "Bucket4j black list ips" to "$BUCKET_4J_BLACK_LIST_IPS:$BUCKET_4J_BLACK_LIST_IPS_DEF_VAL",
        "Bucket4j X-REAL-IP" to "$BUCKET_4J_USE_X_REAL_IP_HEADER:$STRING_FALSE",
        DELIMITER to EMPTY_STRING
    )

    val R2DBC_SET = mapOf(
        "database" to SPRING_R2DBC_URL,
        "pool enabled" to SPRING_R2DBC_POOL_ENABLED,
        "initial pool size" to SPRING_R2DBC_POOL_IS,
        "max pool size" to SPRING_R2DBC_POOL_MS,
        "max life time" to SPRING_R2DBC_MAX_LIFE_TIME,
        "Auto synchronize" to REFERENCES_AUTO_SYNCHRONIZE,
        DELIMITER to EMPTY_STRING
    )

    val KAFKA_SET = mapOf(
        "Kafka" to KAFKA_BOOSTRAP_SERVERS,
        DELIMITER to EMPTY_STRING
    )

    val REDIS_SET = mapOf(
        "Redis host" to SPRING_REDIS_HOST,
        "Redis port" to SPRING_REDIS_PORT,
        DELIMITER to EMPTY_STRING
    )

    val JWT_SET = mapOf(
        "access jwt expiration (chess community)" to JWT_CC_PLAYER_ACCESS_EXPIRATION_TIME,
        "refresh jwt expiration (chess community)" to JWT_CC_PLAYER_REFRESH_EXPIRATION_TIME,
        DELIMITER to EMPTY_STRING
    )

    val MEDIA_SET = mapOf(
        "Minio host" to MINIO_HOST_KEY,
        "Minio access key" to MINIO_ACCESS_KEY,
        "Minio secret key" to MINIO_SECRET_KEY,
        DELIMITER to EMPTY_STRING
    )

    val MAIL_SET = mapOf(
        "Mail host" to MAIL_HOST_KEY,
        "Mail port" to MAIL_PORT_KEY,
        "Mail username" to MAIL_USER_KEY,
        "Mail protocol" to MAIL_PROTOCOL_KEY,
        "Mail from" to MAIL_FROM_KEY,
        "Batch limit" to SPRING_MAIL_PROCESS_BATCH_LIMIT,
        "Batch interval" to SPRING_MAIL_PROCESS_BATCH_INTERVAL,
        "Batch interval" to SPRING_MAIL_PROCESS_BATCH_INTERVAL,
        "Async sending alg" to SPRING_MAIL_ASYNC_SEND_ALG,
        DELIMITER to EMPTY_STRING
    )

    val WEB_CLIENT_SET = mapOf(
        "Web client max connections" to WEB_CLIENT_MAX_CONNECTIONS,
        "Web client max idle time" to WEB_CLIENT_MAX_IDLE_TIME,
        "Web client max life time" to WEB_CLIENT_MAX_LIFE_TIME,
        "Web client max acquire time" to WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT,
        "Web client evict in background" to WEB_CLIENT_EVICT_IN_BACKGROUND,
        "Web client test connection" to WEB_CLIENT_TEST_CONNECTION,
        DELIMITER to EMPTY_STRING
    )

    val GOOGLE_CLIENT_SET = mapOf(
        "Google server" to GOOGLE_SERVER,
        "Verify captcha route" to GOOGLE_SERVER_RECAPTCHA_V3_ROUTES_SITE_VERIFY,
        "Google captcha v3 secret" to GOOGLE_SERVER_RECAPTCHA_V3_SECRET,
        DELIMITER to EMPTY_STRING
    )

    val CUSTOM_SET = mapOf(
        CUSTOM_BANNER_SET_KEY to EMPTY_STRING
    )
}
