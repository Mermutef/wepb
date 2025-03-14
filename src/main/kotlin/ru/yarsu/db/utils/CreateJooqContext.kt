package ru.yarsu.db.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import ru.yarsu.config.DatabaseConfig

fun createJooqContext(
    config: DatabaseConfig,
    showJooqDebug: Boolean = false,
): DSLContext {
    val dataSource = hikariFromDatabaseConfig(config)
    val jooqSettings = Settings()
    jooqSettings.isExecuteLogging = showJooqDebug
    jooqSettings.isDiagnosticsLogging = showJooqDebug
    return DSL.using(
        dataSource,
        SQLDialect.POSTGRES,
        jooqSettings,
    )
}

fun hikariFromDatabaseConfig(config: DatabaseConfig): HikariDataSource {
    val hikariConfig = HikariConfig()
    hikariConfig.jdbcUrl = config.jdbc
    hikariConfig.username = config.user
    hikariConfig.password = config.password
    return HikariDataSource(hikariConfig)
}
