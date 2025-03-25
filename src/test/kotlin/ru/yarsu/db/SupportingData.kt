package ru.yarsu.db

import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.PasswordHasher

@Suppress("MayBeConst")
val validName = "Иван"

@Suppress("MayBeConst")
val validPass = "passwordiche"

@Suppress("MayBeConst")
val validEmail = "validEmail@gmail.com"

@Suppress("MayBeConst")
val validUserSurname = "valid-user-surname"

@Suppress("MayBeConst")
val validLogin = "valid-login"

@Suppress("MayBeConst")
val validPhoneNumber = "+7(999)256-23-54"

@Suppress("MayBeConst")
val validVKLink = "https://vk.com/id564657468"

val appConfig = AppConfig.fromEnvironment()
val appConfiguredPasswordHasher = PasswordHasher(appConfig.authConfig)
