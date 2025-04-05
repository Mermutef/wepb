package ru.yarsu.domain.models

import ru.yarsu.domain.accounts.Role

data class User (
    val id: Int,
    val name: String,
    val surname: String,
    val login: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val vkLink: String?,
    val role: Role,
) {
    companion object {
        fun validateUserData(
            login: String,
            email: String,
            password: String,
        ): UserValidationResult =
            when {
                login.isBlank() -> UserValidationResult.LOGIN_IS_BLANK_OR_EMPTY
                login.length > MAX_LOGIN_LENGTH -> UserValidationResult.LOGIN_IS_TOO_LONG
                email.isBlank() -> UserValidationResult.EMAIL_IS_BLANK_OR_EMPTY
                email.length > MAX_EMAIL_LENGTH -> UserValidationResult.EMAIL_IS_TOO_LONG
                !emailPattern.matches(email) -> UserValidationResult.EMAIL_PATTERN_MISMATCH
                password.isBlank() -> UserValidationResult.PASSWORD_IS_BLANK_OR_EMPTY
                !loginPattern.matches(login) -> UserValidationResult.LOGIN_PATTERN_MISMATCH

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

        const val MAX_LOGIN_LENGTH = 30

        const val MAX_EMAIL_LENGTH = 100

        val emailPattern = Regex("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$") // mail.aaa-ssss_121323@uniyar.ac.ru
        val loginPattern = Regex("^[\\w-.]+\$")
    }
}

enum class UserValidationResult {
    LOGIN_IS_BLANK_OR_EMPTY,
    LOGIN_IS_TOO_LONG,
    LOGIN_PATTERN_MISMATCH,
    EMAIL_IS_BLANK_OR_EMPTY,
    EMAIL_IS_TOO_LONG,
    EMAIL_PATTERN_MISMATCH,
    PASSWORD_IS_BLANK_OR_EMPTY,
    ALL_OK,
}
