package ru.yarsu.db

import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.PasswordHasher

@Suppress("MayBeConst")
val validUserName = "valid-user-name"

@Suppress("MayBeConst")
val validPass = "pass"

@Suppress("MayBeConst")
val validEmail = "validEmail@gmail.com"

val appConfig = AppConfig.fromEnvironment()
val appConfiguredPasswordHasher = PasswordHasher(appConfig.authConfig)
