package ru.yarsu.domain.operations

import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.Role

@Suppress("MayBeConst")
val validName = "Иван"

@Suppress("MayBeConst")
val validPass = "passwordiche"

@Suppress("MayBeConst")
val validEmail = "validEmail@gmail.com"

@Suppress("MayBeConst")
val validUserSurname = "Иванов"

@Suppress("MayBeConst")
val validLogin = "valid-login"

@Suppress("MayBeConst")
val validPhoneNumber = "+7(999)256-23-54"

@Suppress("MayBeConst")
val validSecondPhoneNumber = "+7(998)256-23-54"

@Suppress("MayBeConst")
val validVKLink = "https://vk.com/id564657468"

val adminRole = Role.ADMIN

val config = AppConfig.fromEnvironment()
