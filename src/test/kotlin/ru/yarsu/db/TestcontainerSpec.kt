package ru.yarsu.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.ContainerLifecycleMode
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import ru.yarsu.config.AppConfig

const val MAX_POOL_SIZE = 80
const val IDLE_TIMEOUT = 10000L

abstract class TestcontainerSpec(body: FunSpec.(context: DSLContext) -> Unit = {}) : FunSpec() {

    init {
        val config = AppConfig.fromEnvironment()

        val ds: HikariDataSource = when (config.testsConfig.useTestContainers) {
            true -> sourceForTestContainers(config)
            false -> sourceForDBMS(config)
        }

        val context = DSL.using(ds, SQLDialect.POSTGRES)

        beforeEach {
            val flyway = Flyway.configure()
                .dataSource(ds)
                .locations(config.testsConfig.migrationsLocation)
                .cleanDisabled(false)
                .load()
            flyway.clean()
            flyway.migrate()
        }

        afterSpec {
            ds.close()
        }

        body(context)
    }

    private fun sourceForTestContainers(config: AppConfig): HikariDataSource =
        PostgreSQLContainer<Nothing>(
            config.testsConfig.postgresImage
        ).apply {
            startupAttempts = 1
        }.let { postgres ->
            install(
                JdbcDatabaseContainerExtension(postgres, ContainerLifecycleMode.Spec)
            ) {
                maximumPoolSize = MAX_POOL_SIZE
                idleTimeout = IDLE_TIMEOUT
            }
        }

    private fun sourceForDBMS(config: AppConfig) =
        HikariConfig().apply {
            jdbcUrl = config.databaseConfig.jdbc
            username = config.databaseConfig.user
            password = config.databaseConfig.password
            maximumPoolSize = MAX_POOL_SIZE
            idleTimeout = IDLE_TIMEOUT
        }.let { hikariConfig ->
            HikariDataSource(hikariConfig)
        }
}
