package ru.yarsu.domain.models

import ru.yarsu.domain.accounts.Status
import java.time.ZonedDateTime

data class Post(
    val id: Int,
    val title: String,
    val preview: String,
    val content: String,
    val hashtag: Hashtag,
    val eventDate: ZonedDateTime?,
    val creationDate: ZonedDateTime,
    val lastModifiedDate: ZonedDateTime,
    val authorId: Int,
    val moderatorId: Int,
    val status: Status,
) {
    companion object {
        fun validatePostData(
            title: String,
            textBody: String
        ): PostValidationResult =
            validateTitle(title)
                ?: validateTextBody(textBody)
                ?: PostValidationResult.ALL_OK

        fun validateTitle(title: String) : PostValidationResult? {
            return when {
                title.isBlank() -> PostValidationResult.TITLE_IS_BLANK_OR_EMPTY
                title.length > MAX_TITLE_LENGTH -> PostValidationResult.TITLE_IS_TOO_LONG
                !titlePattern.matches(title) -> PostValidationResult.TITLE_PATTERN_MISMATCH
                else -> null
            }
        }

        fun validateTextBody(textBody: String): PostValidationResult? {
            return when {
                textBody.isBlank() -> PostValidationResult.TEXT_BODY_IS_BLANK_OR_EMPTY
                textBody.length > MAX_TEXT_BODY_LENGTH -> PostValidationResult.TEXT_BODY_IS_TOO_LONG
                !textBodyPattern.matches(textBody) -> PostValidationResult.TEXT_BODY_PATTERN_MISMATCH
                else -> null
            }
        }

        const val MAX_TITLE_LENGTH = 100
        const val MAX_TEXT_BODY_LENGTH = 2048

        val titlePattern = Regex("^[\\w-.]+\$")
        val textBodyPattern = Regex("^[\\w-.]+\$")
    }
}

enum class PostValidationResult {
    TITLE_IS_BLANK_OR_EMPTY,
    TITLE_IS_TOO_LONG,
    TITLE_PATTERN_MISMATCH,
    TEXT_BODY_IS_BLANK_OR_EMPTY,
    TEXT_BODY_IS_TOO_LONG,
    TEXT_BODY_PATTERN_MISMATCH,
    ALL_OK,
}