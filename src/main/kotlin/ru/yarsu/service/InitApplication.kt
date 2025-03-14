package ru.yarsu.service

import org.flywaydb.core.Flyway
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.operations.OperationsHolder

fun OperationsHolder.initApplication(config: AppConfig) {
    applyMigrations(config)
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

// private fun OperationsHolder.initGeneralAdmin(
//    jooqContext: DSLContext,
//    config: AppConfig,
// ) {
//    when (val generalUser = this.userOperations.fetchUserByName(ADMIN_USERNAME)) {
//        is Success -> generalUser.value
//        is Failure -> jooqContext.insertInto(USERS)
//            .set(USERS.ID, ADMIN_USER_ID)
//            .set(USERS.NAME, ADMIN_USERNAME)
//            .set(USERS.EMAIL, "$ADMIN_USERNAME@gmail.com")
//            .set(
//                USERS.PASSWORD,
//                PasswordHasher(config.authConfig)
//                    .hash(
//                        config.authConfig.generalPassword
//                    )
//            )
//            .set(USERS.ROLE, UserRole.ADMIN)
//            .returningResult()
//            .fetchOne()
//            ?.toUser()
//    } ?: error("Can`t create admin")
// }
