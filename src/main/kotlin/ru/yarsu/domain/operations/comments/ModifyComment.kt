package ru.yarsu.domain.operations.comments

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.Comment

class ChangeVisibilityComment(
    private val changeVisibility: (commentId: Int) -> Comment?,
) : (Comment) -> Result4k<Comment, FieldInCommentChangingError> {
    override operator fun invoke(comment: Comment): Result4k<Comment, FieldInCommentChangingError> =
        try {
            when (val commentWithNewVisibility = changeVisibility(comment.id)) {
                is Comment -> Success(commentWithNewVisibility)
                else -> Failure(FieldInCommentChangingError.UNKNOWN_CHANGING_ERROR)
            }
        } catch (_: DataAccessException) {
            Failure(FieldInCommentChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

class ChangeContentInComment(
    private val changeContent: (commentID: Int, newContent: String) -> Comment?,
) : (Comment, String) -> Result4k<Comment, FieldInCommentChangingError> {
    override operator fun invoke(
        comment: Comment,
        newContent: String,
    ): Result4k<Comment, FieldInCommentChangingError> =
        try {
            when {
                newContent.isBlank() ->
                    Failure(FieldInCommentChangingError.CONTENT_IS_BLANK_OR_EMPTY)
                newContent.length > Comment.MAX_CONTENT_LENGTH ->
                    Failure(FieldInCommentChangingError.CONTENT_IS_TOO_LONG)
                else -> when (val commentWithNewContent = changeContent(comment.id, newContent)) {
                    is Comment -> Success(commentWithNewContent)
                    else -> Failure(FieldInCommentChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(FieldInCommentChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class FieldInCommentChangingError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_CHANGING_ERROR,
    CONTENT_IS_BLANK_OR_EMPTY,
    CONTENT_IS_TOO_LONG,
}
