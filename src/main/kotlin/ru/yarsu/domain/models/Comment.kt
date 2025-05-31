package ru.yarsu.domain.models

import java.time.ZonedDateTime

data class Comment(
    val id: Int,
    val content: String,
    val authorId: Int,
    val postId: Int,
    val creationDate: ZonedDateTime,
    val lastModifiedDate: ZonedDateTime,
    val isHidden: Boolean
) {
    companion object {
        fun validateCommentData(content: String): CommentValidationResult =
            validateContent(content)
                ?: CommentValidationResult.ALL_OK

        fun validateContent(content: String): CommentValidationResult? {
            return when {
                content.isBlank() -> CommentValidationResult.CONTENT_IS_BLANK_OR_EMPTY
                content.length > MAX_CONTENT_LENGTH -> CommentValidationResult.CONTENT_IS_TOO_LONG
                else -> null
            }
        }

        const val MAX_CONTENT_LENGTH = 256
    }
}

enum class CommentValidationResult {
    CONTENT_IS_BLANK_OR_EMPTY,
    CONTENT_IS_TOO_LONG,
    ALL_OK,
}
