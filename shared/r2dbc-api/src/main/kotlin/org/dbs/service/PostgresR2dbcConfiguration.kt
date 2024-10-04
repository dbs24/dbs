package org.dbs.service

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory

abstract class PostgresR2dbcConfiguration : GenericR2dbcConfiguration() {
    override fun connectionFactory(): ConnectionFactory = url.run {
        PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder().build()).also {
            logger.info("Initialize database connection factory: $this (${it.javaClass.canonicalName})")
        }
    }
}
