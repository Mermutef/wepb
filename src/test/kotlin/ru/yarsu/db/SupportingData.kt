package ru.yarsu.db

import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.PasswordHasher
import java.time.ZonedDateTime

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

val appConfig = AppConfig.fromEnvironment()
val appConfiguredPasswordHasher = PasswordHasher(appConfig.authConfig)

@Suppress("MayBeConst")
val validPostTitle = "validTitle"

@Suppress("MayBeConst")
val validPostPreview = "validPreview"

@Suppress("MayBeConst")
val validPostContent = "validContent"

@Suppress("MayBeConst")
val validPostDate1 = ZonedDateTime.parse("2024-06-15T15:00:00+03:00").toOffsetDateTime().toZonedDateTime()

@Suppress("MayBeConst")
val validPostDate2 = ZonedDateTime.parse("2025-06-15T15:00:00+03:00").toOffsetDateTime().toZonedDateTime()

@Suppress("MayBeConst")
val validHashtagTitle = "validTitle"
