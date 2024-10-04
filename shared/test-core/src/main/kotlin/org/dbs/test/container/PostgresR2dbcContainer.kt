package org.dbs.test.container

import api.TestConst.TC_POSTGRES_IMAGE_DEF
import org.dbs.application.core.service.funcs.StringFuncs.createRandomString
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_PASSWORD
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_URL
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_USER_NAME
import org.dbs.test.core.AbstractTestContainer
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer
import java.util.*


class PostgresR2dbcContainer(imageName: String, databaseName: String, databaseUser: String, script: String) :
    AbstractTestContainer() {
    val dbPgContainer: PostgreSQLContainer<*>
    private val pgR2dbcContainer: PostgreSQLR2DBCDatabaseContainer

    init {
        logger.info("Init postgres containers: $imageName")
        dbPgContainer = PostgreSQLContainer(imageName)
            .withDatabaseName(databaseName)
            .withUsername(databaseUser)
            .withPassword(createRandomString(10))
            .withInitScript(script)
            .withReuse(true)
        pgR2dbcContainer = PostgreSQLR2DBCDatabaseContainer(dbPgContainer)
        dbPgContainer.start()
        pgR2dbcContainer.start()
        require(dbPgContainer.isRunning) {
            logger.error("Container not running: ${dbPgContainer.containerName}".uppercase(Locale.getDefault()))
        }
    }

    constructor(databaseName: String, databaseUser: String, script: String) : this(
        TC_POSTGRES_IMAGE_DEF,
        databaseName,
        databaseUser,
        script
    )

    override fun overrideApplicationProperties(registry: DynamicPropertyRegistry) = with(registry) {
        val postgresUrl = "postgresql://${dbPgContainer.host}:${dbPgContainer.firstMappedPort}/${dbPgContainer.databaseName}"
        add(SPRING_R2DBC_URL) { "r2dbc:$postgresUrl" }
        add(SPRING_R2DBC_USER_NAME) { dbPgContainer.username }
        add(SPRING_R2DBC_PASSWORD) { dbPgContainer.password }
    }
}
