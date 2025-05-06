package ru.yarsu.domain.models

import ru.yarsu.domain.accounts.Status
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class Post(
    val id: Int,
    val title: String,
    val preview: String,
    val content: String,
    val hashtagId: Int,
//    val eventDate: ZonedDateTime?,
//    val creationDate: ZonedDateTime,
//    val lastModifiedDate: ZonedDateTime,
    val eventDate: LocalDateTime?,
    val creationDate: LocalDateTime,
    val lastModifiedDate: LocalDateTime,
    val authorId: Int,
    val moderatorId: Int,
    val status: Status,
) {
    companion object {
        fun validatePostData(
            title: String,
            preview: String,
            content: String,
        ): PostValidationResult =
            validateTitle(title)
                ?: validatePreview(preview)
                ?: validateContent(content)
                ?: PostValidationResult.ALL_OK

        fun validateTitle(title: String) : PostValidationResult? {
            return when {
                title.isBlank() -> PostValidationResult.TITLE_IS_BLANK_OR_EMPTY
                title.length > MAX_TITLE_LENGTH -> PostValidationResult.TITLE_IS_TOO_LONG
                !titlePattern.matches(title) -> PostValidationResult.TITLE_PATTERN_MISMATCH
                else -> null
            }
        }

        fun validatePreview(preview: String) : PostValidationResult? {
            return when {
                preview.isBlank() -> PostValidationResult.PREVIEW_IS_BLANK_OR_EMPTY
                preview.length > MAX_PREVIEW_LENGTH -> PostValidationResult.PREVIEW_IS_TOO_LONG
                !previewPattern.matches(preview) -> PostValidationResult.PREVIEW_PATTERN_MISMATCH
                else -> null
            }
        }

        fun validateContent(content: String): PostValidationResult? {
            return when {
                content.isBlank() -> PostValidationResult.CONTENT_IS_BLANK_OR_EMPTY
                else -> null
            }
        }

        const val MAX_TITLE_LENGTH = 100
        const val MAX_PREVIEW_LENGTH = 256

        val titlePattern = Regex("^[\\w-.]+\$")
        val previewPattern = Regex("^[\\w-.]+$")
    }
}

enum class PostValidationResult {
    TITLE_IS_BLANK_OR_EMPTY,
    TITLE_IS_TOO_LONG,
    TITLE_PATTERN_MISMATCH,
    PREVIEW_IS_BLANK_OR_EMPTY,
    PREVIEW_IS_TOO_LONG,
    PREVIEW_PATTERN_MISMATCH,
    CONTENT_IS_BLANK_OR_EMPTY,
    ALL_OK,
}