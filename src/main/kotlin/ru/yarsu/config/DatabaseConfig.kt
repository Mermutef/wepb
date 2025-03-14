package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.int
import org.http4k.lens.nonBlankString

data class DatabaseConfig (
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val base: String,
) {
    val jdbc get() = "jdbc:postgresql://$host:$port/$base"

    companion object {
        private val hostLens = EnvironmentKey.nonBlankString().required(
            "db.host",
            "Name of the server that runs the DBMS",
        )

        private val portLens = EnvironmentKey.int().defaulted(
            "db.port",
            5432,
            "DBMS port",
        )

        private val userLens = EnvironmentKey.nonBlankString().required(
            "db.user",
            "Name of the user for DBMS connection",
        )

        private val passwordLens = EnvironmentKey.nonBlankString().required(
            "db.password",
            "DBMS user password"
        )

        private val baseLens = EnvironmentKey.nonBlankString().required(
            "db.base",
            "Database in the DBMS to be used",
        )

        fun fromEnvironment(environment: Environment) =
            DatabaseConfig(
                hostLens(environment),
                portLens(environment),
                userLens(environment),
                passwordLens(environment),
                baseLens(environment),
            )
    }
}
