package ru.yarsu.service

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import ru.yarsu.ADMIN_USERNAME
import ru.yarsu.ADMIN_USER_ID
import ru.yarsu.config.AppConfig
import ru.yarsu.db.generated.enums.UserRole
import ru.yarsu.db.generated.tables.Users.Companion.USERS
import ru.yarsu.domain.accounts.PasswordHasher
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.operations.OperationsHolder

fun OperationsHolder.initApplication(jooqContext: DSLContext, config: AppConfig) {
    applyMigrations(config)
    initGeneralAdmin(jooqContext, config)
}

fun applyMigrations(config: AppConfig) {
    val flyway = Flyway
        .configure()
        .dataSource(config.databaseConfig.jdbc, config.databaseConfig.user, config.databaseConfig.password)
        .locations("classpath:migrations")
        .cleanDisabled(true)
        .validateMigrationNaming(true)
        .load()

    if (flyway.info().pending().isNotEmpty()) flyway.migrate()
}

private fun OperationsHolder.initGeneralAdmin(
    jooqContext: DSLContext,
    config: AppConfig,
) {
    when (val generalUser = this.userOperations.fetchUserByLogin(ADMIN_USERNAME)) {
        is Success -> generalUser.value
        is Failure -> jooqContext.insertInto(USERS)
            .set(USERS.ID, ADMIN_USER_ID)
            .set(USERS.LOGIN, ADMIN_USERNAME)
            .set(USERS.EMAIL, "$ADMIN_USERNAME@gmail.com")
            .set(
                USERS.PASSWORD,
                PasswordHasher(config.authConfig)
                    .hash(
                        config.authConfig.generalPassword
                    )
            )
            .set(USERS.ROLE, UserRole.ADMIN)
            .set(USERS.NAME, "Администратор")
            .set(USERS.SURNAME, "Платформы")
            .set(USERS.PHONENUMBER, "79345635651")
            .set(USERS.ROLE, UserRole.ADMIN)
            .returningResult()
            .fetchOne()
    } ?: error("Can`t create admin")
}
