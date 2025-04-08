package ru.yarsu

import org.http4k.core.*
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.config.AppConfig
import ru.yarsu.db.DatabaseOperationsHolder
import ru.yarsu.db.utils.createJooqContext
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.service.initApplication
import ru.yarsu.web.createApp

const val ADMIN_USERNAME = "admin"
const val ADMIN_USER_ID = -1
const val MAIN_CLASS = "ru.yarsu.Main"
const val JWT_ISSUER = "Wepb"

fun main() {
    val config = AppConfig.fromEnvironment()
    val jooqContext = createJooqContext(config.databaseConfig)
    val database = DatabaseOperationsHolder(jooqContext)
    val operations = OperationsHolder(database, config)
    operations.initApplication(config)

    val app = createApp(operations, config)

    val filters = Filter.NoOp

    val server = filters
        .then(app)
        .asServer(Netty(config.webConfig.port)).start()
    println("Running on port http://localhost:${server.port()}/")
}
