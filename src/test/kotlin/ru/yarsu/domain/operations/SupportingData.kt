package ru.yarsu.domain.operations

import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.Role

@Suppress("MayBeConst")
val validUserName = "valid-user-name"

@Suppress("MayBeConst")
val validPass = "pass"

@Suppress("MayBeConst")
val validEmail = "validEmail@gmail.com"

val adminRole = Role.ADMIN

val config = AppConfig.fromEnvironment()
