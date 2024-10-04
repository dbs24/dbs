package api

object TestConst {
    const val REPEATED_TEST_AMOUNT = 13
    const val REPEATED_KOTEST_AMOUNT = REPEATED_TEST_AMOUNT * REPEATED_TEST_AMOUNT//*REPEATED_TEST_AMOUNT

    var PLAYER_TEST_AMOUNT = 1
    //const val REPEATED_TEST_AMOUNT = 1
    const val SHORT_REPEATED_TEST_AMOUNT = 3

    //==================================================================================================================
    const val TC_POSTGRES_IMAGE_DEF = "postgres:15.3"
    const val TC_KAFKA_IMAGE_DEF = "confluentinc/cp-kafka:6.2.1"
    const val TC_MINIO_IMAGE_DEF = "minio/minio:RELEASE.2022-09-01T23-53-36Z"
    const val TC_MINIO_PORT_DEF = 9000
    const val TC_MINIO_CONSOLE_PORT_DEF = 9001
    const val TC_MINIO_ACCESS_KEY = "tcMinioAccessKey"
    const val TC_MINIO_SECRET_KEY = "tcMinioSecretKey"
    const val TC_REDIS_IMAGE_DEF = "redis:5.0.3-alpine"
    const val TC_REDIS_PORT_DEF = 6379

    //==================================================================================================================
    const val SQL_TEST_DB_NAME = "db4test"
    const val SQL_TEST_DB_USER = "fakedUser"
    const val SQL_CHESS24_MGMT_DB_SCRIPT = "sql/create_cc_mgmt_scheme.sql"
    const val SQL_CHESS24_SANDBOX_DB_SCRIPT = "sql/create_cc_sandbox_scheme.sql"
    const val SQL_OUT_OF_SERVICE_CORE = "sql/create_out_of_service_scheme.sql"
}
