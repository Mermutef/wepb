package ru.yarsu.domain.models

data class Direction (
    val id: Int,
    val name: String,
    val description: String,
    val logoPath: String,
    val bannerPath: String,
    val chairman_id: Int,
    val deputy_сhairman_id: Int
) {
    companion object {
        @Suppress("LongParameterList", "CyclomaticComplexMethod")
        fun validateDirectionData(
            name: String,
            description: String,
            logoPath: String,
            bannerPath: String,
            chairman_id: Int,
            deputy_сhairman_id: Int
        ): DirectionValidationResult =
            validateName(name)
                ?: validateDescription(description)
                ?: validatePath(logoPath)
                ?: validatePath(bannerPath)
                ?: DirectionValidationResult.ALL_OK

        fun validateName(name: String): DirectionValidationResult? {
            return when {
                name.isBlank() -> DirectionValidationResult.NAME_IS_BLANK_OR_EMPTY
                name.length > MAX_NAME_LENGTH -> DirectionValidationResult.NAME_IS_TOO_LONG
                !namePattern.matches(name) -> DirectionValidationResult.NAME_PATTERN_MISMATCH
                else -> null
            }
        }

        fun validateDescription(description: String): DirectionValidationResult? {
            return when {
                description.isBlank() -> DirectionValidationResult.DESCRIPTION_IS_BLANK_OR_EMPTY
                !descriptionPattern.matches(description) -> DirectionValidationResult.DESCRIPTION_PATTERN_MISMATCH
                else -> null
            }
        }

        fun validatePath(path: String): DirectionValidationResult? {
            return when {
                path.isBlank() -> DirectionValidationResult.PATH_IS_BLANK_OR_EMPTY
                else -> null
            }
        }

        const val MAX_NAME_LENGTH = 255

        val namePattern = Regex("^[а-яА-Я -]+\$")
        val descriptionPattern = Regex("^[\\w-.]+\$")
    }
}

enum class DirectionValidationResult {
    NAME_IS_BLANK_OR_EMPTY,
    NAME_IS_TOO_LONG,
    NAME_PATTERN_MISMATCH,
    DESCRIPTION_IS_BLANK_OR_EMPTY,
    DESCRIPTION_PATTERN_MISMATCH,
    PATH_IS_BLANK_OR_EMPTY,
    ALL_OK
}