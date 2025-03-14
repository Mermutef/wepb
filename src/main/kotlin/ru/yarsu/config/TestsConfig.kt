package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.boolean
import org.http4k.lens.nonBlankString

data class TestsConfig (
    val useTestContainers: Boolean,
    val postgresImage: String,
    val migrationsLocation: String,
) {
    companion object {
        fun fromEnvironment(environment: Environment) =
            TestsConfig(
                useTestContainersLens(environment),
                postgresImageLens(environment),
                migrationsLocationLens(environment),
            )

        private val useTestContainersLens = EnvironmentKey.boolean().defaulted(
            "tests.useTestContainers",
            true,
            "Run tests using TestContainers or using the database configured in app",
        )

        private val postgresImageLens = EnvironmentKey.nonBlankString().defaulted(
            "tests.postgresImage",
            "postgres:16.3",
            "Docker image to run testcontainers with"
        )

        private val migrationsLocationLens = EnvironmentKey.nonBlankString().defaulted(
            "tests.migrationsLocation",
            "classpath:migrations",
            "Path to migrations to be applied to database before each test"
        )
    }
}
