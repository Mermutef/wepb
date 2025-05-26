package ru.yarsu.domain.operations.posts

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import ru.yarsu.domain.models.User
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
                !pattern.matches(newField) ->
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

class ChangeHashtagIdInPost(
    private val changeHashtagId: (postID: Int, newHashtagId: Int) -> Post?,
    private val selectHashtagById: (id: Int) -> Hashtag?,
) : (Post, Int) -> Result4k<Post, FieldInPostChangingError> {
    override operator fun invoke(
        post: Post,
        newHashtagId: Int,
    ): Result4k<Post, FieldInPostChangingError> {
        return try {
            when {
                selectHashtagById(newHashtagId) == null
                -> Failure(FieldInPostChangingError.HASHTAG_NOT_EXISTS)

                else -> when (val postWithNewHashtag = changeHashtagId(post.id, newHashtagId)) {
                    is Post -> Success(postWithNewHashtag)
                    else -> Failure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(FieldInPostChangingError.UNKNOWN_DATABASE_ERROR)
        }
    }
}

class ChangeUserIdInPost(
    private val changeUserId: (postId: Int, newUserId: Int) -> Post?,
    private val selectUserById: (id: Int) -> User?,
) : (Post, Int) -> Result4k<Post, FieldInPostChangingError> {
    override operator fun invoke(
        post: Post,
        newUserId: Int,
    ): Result4k<Post, FieldInPostChangingError> {
        return try {
            when {
                selectUserById(newUserId) == null
                -> Failure(FieldInPostChangingError.USER_NOT_EXISTS)

                else -> when (val postWithNewUserId = changeUserId(post.id, newUserId)) {
                    is Post -> Success(postWithNewUserId)
                    else -> Failure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(FieldInPostChangingError.UNKNOWN_DATABASE_ERROR)
        }
    }
}

class ChangePreviewInPost(
    private val changePreview: (postId: Int, newPreview: String) -> Post?,
    private val selectMediaByName: (name: String) -> MediaFile?,
) : (Post, String) -> Result4k<Post, FieldInPostChangingError> {
    override operator fun invoke(
        post: Post,
        newPreview: String,
    ): Result4k<Post, FieldInPostChangingError> {
        return try {
            when {
                newPreview.length > Post.MAX_PREVIEW_LENGTH
                -> Failure(FieldInPostChangingError.FIELD_IS_TOO_LONG)

                selectMediaByName(newPreview) == null
                -> Failure(FieldInPostChangingError.MEDIA_NOT_EXISTS)

                else -> when (val postWithNewPreview = changePreview(post.id, newPreview)) {
                    is Post -> Success(postWithNewPreview)
                    else -> Failure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(FieldInPostChangingError.UNKNOWN_DATABASE_ERROR)
        }
    }
}

@Suppress("detekt:CyclomaticComplexMethod")
class ChangePost(
    private val changePost: (
        postID: Int,
        newTitle: String,
        newPreview: String,
        newContent: String,
        newHashtagId: Int,
        newEventDate: ZonedDateTime?,
        newAuthorId: Int,
        newModeratorId: Int?,
    ) -> Post?,
    private val selectHashtagById: (id: Int) -> Hashtag?,
    private val selectUserById: (id: Int) -> User?,
    private val selectMediaByName: (name: String) -> MediaFile?,
) : (Post, String, String, String, Int, ZonedDateTime?, Int, Int?)
    -> Result4k<Post, FieldInPostChangingError> {
    override operator fun invoke(
        post: Post,
        newTitle: String,
        newPreview: String,
        newContent: String,
        newHashtagId: Int,
        newEventDate: ZonedDateTime?,
        newAuthorId: Int,
        newModeratorId: Int?,
    ): Result4k<Post, FieldInPostChangingError> {
        try {
            return when {
                newTitle.isBlank() ->
                    Failure(FieldInPostChangingError.TITLE_IS_BLANK_OR_EMPTY)
                newTitle.length > Post.MAX_TITLE_LENGTH ->
                    Failure(FieldInPostChangingError.TITLE_IS_TOO_LONG)
                newPreview.isBlank() ->
                    Failure(FieldInPostChangingError.PREVIEW_IS_BLANK_OR_EMPTY)
                newPreview.length > Post.MAX_PREVIEW_LENGTH ->
                    Failure(FieldInPostChangingError.PREVIEW_IS_TOO_LONG)
                newContent.isBlank() ->
                    Failure(FieldInPostChangingError.CONTENT_IS_BLANK_OR_EMPTY)
                selectHashtagById(newHashtagId) == null ->
                    Failure(FieldInPostChangingError.HASHTAG_NOT_EXISTS)
                selectUserById(newAuthorId) == null ->
                    Failure(FieldInPostChangingError.USER_NOT_EXISTS)
                newModeratorId != null && selectUserById(newModeratorId) == null ->
                    Failure(FieldInPostChangingError.USER_NOT_EXISTS)
                selectMediaByName(newPreview) == null ->
                    Failure(FieldInPostChangingError.MEDIA_NOT_EXISTS)
                else -> when (
                    val newPost = changePost(
                        post.id,
                        newTitle,
                        newPreview,
                        newContent,
                        newHashtagId,
                        newEventDate,
                        newAuthorId,
                        newModeratorId,
                    )
                ) {
                    is Post -> Success(newPost)
                    else -> Failure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
                }
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
    USER_NOT_EXISTS,
    MEDIA_NOT_EXISTS,
    TITLE_IS_BLANK_OR_EMPTY,
    TITLE_IS_TOO_LONG,
    PREVIEW_IS_BLANK_OR_EMPTY,
    PREVIEW_IS_TOO_LONG,
    CONTENT_IS_BLANK_OR_EMPTY,
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
