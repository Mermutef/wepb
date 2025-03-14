package ru.yarsu.domain.models

import ru.yarsu.domain.accounts.Role

data class User (
    val id: Int,
    val name: String,
    val email: String,
    val pass: String,
    val role: Role,
) {
    companion object {
        fun validateUserData(
            name: String,
            email: String,
            pass: String,
        ): UserValidationResult =
            when {
                name.isBlank() -> UserValidationResult.NAME_IS_BLANK_OR_EMPTY
                name.length > MAX_NAME_LENGTH -> UserValidationResult.NAME_IS_TOO_LONG
                email.isBlank() -> UserValidationResult.EMAIL_IS_BLANK_OR_EMPTY
                email.length > MAX_EMAIL_LENGTH -> UserValidationResult.EMAIL_IS_TOO_LONG
                !emailPattern.matches(email) -> UserValidationResult.EMAIL_PATTERN_MISMATCH
                pass.isBlank() -> UserValidationResult.PASSWORD_IS_BLANK_OR_EMPTY
                !namePattern.matches(name) -> UserValidationResult.NAME_PATTERN_MISMATCH

                else -> UserValidationResult.ALL_OK
            }

        fun validateUserDataName(
            name: String,
            pass: String,
        ): UserValidationResult =
            when {
                name.isBlank() -> UserValidationResult.NAME_IS_BLANK_OR_EMPTY
                name.length > MAX_NAME_LENGTH -> UserValidationResult.NAME_IS_TOO_LONG
                !namePattern.matches(name) -> UserValidationResult.NAME_PATTERN_MISMATCH
                pass.isBlank() -> UserValidationResult.PASSWORD_IS_BLANK_OR_EMPTY
                else -> UserValidationResult.ALL_OK
            }

        fun validateUserDataEmail(
            email: String,
            pass: String,
        ): UserValidationResult =
            when {
                email.isBlank() -> UserValidationResult.EMAIL_IS_BLANK_OR_EMPTY
                email.length > MAX_NAME_LENGTH -> UserValidationResult.EMAIL_IS_TOO_LONG
                !emailPattern.matches(email) -> UserValidationResult.EMAIL_PATTERN_MISMATCH
                pass.isBlank() -> UserValidationResult.PASSWORD_IS_BLANK_OR_EMPTY
                else -> UserValidationResult.ALL_OK
            }

        const val MAX_NAME_LENGTH = 30

        const val MAX_EMAIL_LENGTH = 100

        val emailPattern = Regex("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")
        val namePattern = Regex("^[\\w-.]+\$")
    }
}

enum class UserValidationResult {
    NAME_IS_BLANK_OR_EMPTY,
    NAME_IS_TOO_LONG,
    NAME_PATTERN_MISMATCH,
    EMAIL_IS_BLANK_OR_EMPTY,
    EMAIL_IS_TOO_LONG,
    EMAIL_PATTERN_MISMATCH,
    PASSWORD_IS_BLANK_OR_EMPTY,
    ALL_OK,
}
