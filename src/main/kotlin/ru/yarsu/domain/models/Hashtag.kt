package ru.yarsu.domain.models

class Hashtag(
    val id: Int,
    val title: String,
) {
    companion object {
        fun validateHashtagData(title: String): HashtagValidationResult =
            validateTitle(title)
                ?: HashtagValidationResult.ALL_OK

        fun validateTitle(title: String): HashtagValidationResult? {
            return when {
                title.isBlank() -> HashtagValidationResult.TITLE_IS_BLANK_OR_EMPTY
                title.length > MAX_TITLE_LENGTH -> HashtagValidationResult.TITLE_IS_TOO_LONG
                !titlePattern.matches(title) -> HashtagValidationResult.TITLE_PATTERN_MISMATCH
                else -> null
            }
        }

        const val MAX_TITLE_LENGTH = 50

        val titlePattern = Regex("^[\\w-.]+\$")
    }
}

enum class HashtagValidationResult {
    TITLE_IS_BLANK_OR_EMPTY,
    TITLE_IS_TOO_LONG,
    TITLE_PATTERN_MISMATCH,
    ALL_OK,
}
