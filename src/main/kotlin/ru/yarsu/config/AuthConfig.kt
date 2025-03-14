package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.nonBlankString
import org.http4k.lens.string

class AuthConfig (
    val salt: String,
    val secret: String,
    val generalPassword: String,
) {
    companion object {
        fun fromEnvironment(environment: Environment) =
            AuthConfig(
                saltLens(environment),
                secretLens(environment),
                generalPasswordLens(environment),
            )

        private val saltLens = EnvironmentKey.string().required(
            "auth.salt",
            "Salt the application use to hash passwords",
        )

        private val secretLens = EnvironmentKey.string().required(
            "auth.secret",
            "Secret the application use",
        )

        private val generalPasswordLens = EnvironmentKey.nonBlankString().required(
            "auth.adminPass",
            "Password for general admin",
        )
    }
}
