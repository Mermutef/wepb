package ru.yarsu.domain.operations.posts

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import java.time.ZonedDateTime

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
                else -> when (val postWithNewStringField = changeField(post.id, newField)) {
                    is Post -> Success(postWithNewStringField)
                    else -> Failure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(FieldInPostChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

class ChangeIntFieldInPost(
    private val changeField: (postID: Int, newField: Int) -> Post?,
) : (Post, Int) -> Result4k<Post, FieldInPostChangingError> {
    override operator fun invoke(
        post: Post,
        newField: Int,
    ): Result4k<Post, FieldInPostChangingError> =
        try {
            when (val postWithNewIntField = changeField(post.id, newField)) {
                is Post -> Success(postWithNewIntField)
                else -> Failure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
            }
        } catch (_: DataAccessException) {
            Failure(FieldInPostChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

class ChangeDateFieldInPost(
    private val changeField: (postID: Int, newField: ZonedDateTime) -> Post?,
) : (Post, ZonedDateTime) -> Result4k<Post, FieldInPostChangingError> {
    override operator fun invoke(
        post: Post,
        newField: ZonedDateTime,
    ): Result4k<Post, FieldInPostChangingError> =
        try {
            when (val postWithNewDateField = changeField(post.id, newField)) {
                is Post -> Success(postWithNewDateField)
                else -> Failure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
            }
        } catch (_: DataAccessException) {
            Failure(FieldInPostChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

@Suppress("detekt:ReturnCount")
class ChangeHashtagIdInPost(
    private val changeHashtagId: (postID: Int, newHashtagId: Int) -> Post?,
    private val selectHashtagById: (id: Int) -> Hashtag?,
) : (Post, Int) -> Result4k<Post, FieldInPostChangingError> {
    override operator fun invoke(
        post: Post,
        newHashtagId: Int,
    ): Result4k<Post, FieldInPostChangingError> {
        try {
            if (selectHashtagById(newHashtagId) == null) {
                return Failure(FieldInPostChangingError.HASHTAG_NOT_EXISTS)
            }
            return when (val postWithNewHashtag = changeHashtagId(post.id, newHashtagId)) {
                is Post -> Success(postWithNewHashtag)
                else -> Failure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
            }
        } catch (_: DataAccessException) {
            return Failure(FieldInPostChangingError.UNKNOWN_DATABASE_ERROR)
        }
    }
}

enum class FieldInPostChangingError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_CHANGING_ERROR,
    FIELD_IS_BLANK_OR_EMPTY,
    FIELD_IS_TOO_LONG,
    FIELD_PATTERN_MISMATCH,
    HASHTAG_NOT_EXISTS,
}

class StatusChanger<R : Status, E : Enum<E>>(
    private val targetStatus: R,
    private val alreadyHasStatusError: E,
    private val updateStatus: (post: Post, newStatus: Status) -> Post?,
    private val unknownError: E,
) : (Post) -> Result4k<Post, E> {
    override operator fun invoke(post: Post): Result4k<Post, E> {
        if (post.status == targetStatus) {
            return Failure(alreadyHasStatusError)
        }

        return when (val newPost = updateStatus(post, targetStatus)) {
            is Post -> Success(newPost)
            else -> Failure(unknownError)
        }
    }
}

enum class MakeStatusError {
    UNKNOWN_DATABASE_ERROR,
    IS_ALREADY_PUBLISHED,
    IS_ALREADY_HIDDEN,
    IS_ALREADY_MODERATION,
    IS_ALREADY_DRAFT,
}
