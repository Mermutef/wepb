package ru.yarsu.domain.operations.comments

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.Comment

class FetchCommentById (
    private val selectCommentById: (Int) -> Comment?,
) : (Int) -> Result4k<Comment, CommentFetchingError> {
    override operator fun invoke(commentId: Int): Result4k<Comment, CommentFetchingError> =
        try {
            selectCommentById(commentId)?.let { Success(it) }
                ?: Failure(CommentFetchingError.NO_SUCH_COMMENT)
        } catch (_: DataAccessException) {
            Failure(CommentFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchPublishedCommentsInPost (
    private val selectPublishedCommentsInPost: (Int) -> List<Comment>,
) : (Int) -> Result4k<List<Comment>, CommentFetchingError> {
    override operator fun invoke(postId: Int): Result4k<List<Comment>, CommentFetchingError> =
        try {
            Success(selectPublishedCommentsInPost(postId))
        } catch (_: DataAccessException) {
            Failure(CommentFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchHiddenCommentsInPost (
    private val selectHiddenCommentsInPost: (Int) -> List<Comment>,
) : (Int) -> Result4k<List<Comment>, CommentFetchingError> {
    override operator fun invoke(postId: Int): Result4k<List<Comment>, CommentFetchingError> =
        try {
            Success(selectHiddenCommentsInPost(postId))
        } catch (_: DataAccessException) {
            Failure(CommentFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchHiddenCommentsOfUserInPost (
    private val selectHiddenCommentsOfUserInPost: (Int, Int) -> List<Comment>,
) : (Int, Int) -> Result4k<List<Comment>, CommentFetchingError> {
    override operator fun invoke(
        postId: Int,
        authorId: Int,
    ): Result4k<List<Comment>, CommentFetchingError> =
        try {
            Success(selectHiddenCommentsOfUserInPost(postId, authorId))
        } catch (_: DataAccessException) {
            Failure(CommentFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class CommentFetchingError {
    UNKNOWN_DATABASE_ERROR,
    NO_SUCH_COMMENT,
}
