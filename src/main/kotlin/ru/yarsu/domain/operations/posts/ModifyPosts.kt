package ru.yarsu.domain.operations.posts

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User

class ChangeStringFieldInPost(
    private val maxLength: Int,
    private val pattern: Regex,
    private val changeField: (postID: Int, newField: String) -> Post?,
) : (Post, String) -> Result4k<Post, FieldInPostChangingError> {
    override operator fun invoke(
        post: Post,
        newField: String,
    ): Result4k<Post, FieldInPostChangingError> =
        try {
            when {
                newField.isBlank() ->
                    Failure(FieldInPostChangingError.FIELD_IS_BLANK_OR_EMPTY)
                maxLength != 0 && newField.length > maxLength ->
                    Failure(FieldInPostChangingError.FIELD_IS_TOO_LONG)
                pattern != Regex("") && !pattern.matches(newField) ->
                    Failure(FieldInPostChangingError.FIELD_PATTERN_MISMATCH)
                else -> when (val userWithNewPassword = changeField(post.id, newField)) {
                    is Post -> Success(userWithNewPassword)
                    else -> Failure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(FieldInPostChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class FieldInPostChangingError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_CHANGING_ERROR,
    FIELD_IS_BLANK_OR_EMPTY,
    FIELD_IS_TOO_LONG,
    FIELD_PATTERN_MISMATCH,
}