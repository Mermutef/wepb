package ru.yarsu.domain.models

import ru.yarsu.domain.accounts.Role

data class User (
    val id: Int,
    val name: String,
    val surname: String,
    val login: String,
    val email: String,
    val phoneNumber: String, //up
    val password: String, //up
    val vkLink: String?, //up
    val role: Role //UP
) {
    companion object {
        @Suppress("LongParameterList", "CyclomaticComplexMethod")
        fun validateUserData(
            name: String,
            surname: String,
            login: String,
            email: String,
            phoneNumber: String,
            password: String,
            vkLink: String?
        ): UserValidationResult =
            when {
                name.isBlank() -> UserValidationResult.NAME_IS_BLANK_OR_EMPTY
                name.length > MAX_NAME_LENGTH -> UserValidationResult.NAME_IS_TOO_LONG
                !namePattern.matches(name) -> UserValidationResult.NAME_PATTERN_MISMATCH
                surname.isBlank() -> UserValidationResult.SURNAME_IS_BLANK_OR_EMPTY
                surname.length > MAX_SURNAME_LENGTH -> UserValidationResult.SURNAME_IS_TOO_LONG
                !namePattern.matches(surname) -> UserValidationResult.SURNAME_PATTERN_MISMATCH
                login.isBlank() -> UserValidationResult.LOGIN_IS_BLANK_OR_EMPTY
                login.length > MAX_LOGIN_LENGTH -> UserValidationResult.LOGIN_IS_TOO_LONG
                !loginPattern.matches(login) -> UserValidationResult.LOGIN_PATTERN_MISMATCH
                email.isBlank() -> UserValidationResult.EMAIL_IS_BLANK_OR_EMPTY
                email.length > MAX_EMAIL_LENGTH -> UserValidationResult.EMAIL_IS_TOO_LONG
                !emailPattern.matches(email) -> UserValidationResult.EMAIL_PATTERN_MISMATCH
                phoneNumber.isBlank() -> UserValidationResult.PHONE_NUMBER_IS_BLANK_OR_EMPTY
                phoneNumber.length > MAX_PHONE_NUMBER_LENGTH -> UserValidationResult.PHONE_NUMBER_IS_TOO_LONG
//                паттерн для телефона
                password.isBlank() -> UserValidationResult.PASSWORD_IS_BLANK_OR_EMPTY
                password.length > MAX_PASSWORD_LENGTH -> UserValidationResult.PASSWORD_IS_TOO_LONG
                vkLink?.isBlank() ?: true -> UserValidationResult.VK_LINK_IS_BLANK_OR_EMPTY
                vkLink?.let{ it.length > MAX_VKLINK_LENGTH } ?: false -> UserValidationResult.VK_LINK_IS_TOO_LONG
                vkLink?.let { !vkLinkPattern.matches(it) } ?: false -> UserValidationResult.VK_LINK_PATTERN_MISMATCH

                else -> UserValidationResult.ALL_OK
            }

        fun validateUserDataName(
            login: String,
            password: String,
        ): UserValidationResult =
            when {
                login.isBlank() -> UserValidationResult.LOGIN_IS_BLANK_OR_EMPTY
                login.length > MAX_LOGIN_LENGTH -> UserValidationResult.LOGIN_IS_TOO_LONG
                !loginPattern.matches(login) -> UserValidationResult.LOGIN_PATTERN_MISMATCH
                password.isBlank() -> UserValidationResult.PASSWORD_IS_BLANK_OR_EMPTY
                else -> UserValidationResult.ALL_OK
            }

        fun validateUserDataEmail(
            email: String,
            password: String,
        ): UserValidationResult =
            when {
                email.isBlank() -> UserValidationResult.EMAIL_IS_BLANK_OR_EMPTY
                email.length > MAX_EMAIL_LENGTH -> UserValidationResult.EMAIL_IS_TOO_LONG
                !emailPattern.matches(email) -> UserValidationResult.EMAIL_PATTERN_MISMATCH
                password.isBlank() -> UserValidationResult.PASSWORD_IS_BLANK_OR_EMPTY
                else -> UserValidationResult.ALL_OK
            }

        const val MAX_NAME_LENGTH = 64
        const val MAX_SURNAME_LENGTH = 64
        const val MAX_LOGIN_LENGTH = 30
        const val MAX_EMAIL_LENGTH = 100
        const val MAX_PHONE_NUMBER_LENGTH = 11
        const val MAX_PASSWORD_LENGTH = 64
        const val MAX_VKLINK_LENGTH = 255


        val emailPattern = Regex("^[\\w.-]+@([\\w-]+\\.)+[a-zA-Z]{2,}\$") // mail.aaa-ssss_121323@uniyar.ac.ru
        val loginPattern = Regex("^[\\w-.]+\$")
        val vkLinkPattern = Regex("^https:\\/\\/vk.com\\/[\\w.-]+\$")
        val namePattern = Regex("^[а-яА-Я -]+\$")
    }
}

enum class UserValidationResult {
    NAME_IS_BLANK_OR_EMPTY,
    NAME_IS_TOO_LONG,
    NAME_PATTERN_MISMATCH,
    SURNAME_IS_BLANK_OR_EMPTY,
    SURNAME_IS_TOO_LONG,
    SURNAME_PATTERN_MISMATCH,
    LOGIN_IS_BLANK_OR_EMPTY,
    LOGIN_IS_TOO_LONG,
    LOGIN_PATTERN_MISMATCH,
    EMAIL_IS_BLANK_OR_EMPTY,
    EMAIL_IS_TOO_LONG,
    EMAIL_PATTERN_MISMATCH,
    PHONE_NUMBER_IS_BLANK_OR_EMPTY,
    PHONE_NUMBER_IS_TOO_LONG,
//    PHONE_NUMBER_PATTERN_MISMATCH,
    PASSWORD_IS_BLANK_OR_EMPTY,
    PASSWORD_IS_TOO_LONG,
    VK_LINK_IS_BLANK_OR_EMPTY,
    VK_LINK_IS_TOO_LONG,
    VK_LINK_PATTERN_MISMATCH,
    ALL_OK,
}
