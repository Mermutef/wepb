package ru.yarsu.domain.operations.posts

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Post
import java.time.ZonedDateTime

class FetchPostById (
    private val selectPostByID: (Int) -> Post?,
) : (Int) -> Result4k<Post, PostFetchingError> {

    override operator fun invoke(postId: Int): Result4k<Post, PostFetchingError> =
        try {
            when (val post = selectPostByID(postId)) {
                is Post -> Success(post)
                else -> Failure(PostFetchingError.NO_SUCH_POST)
            }
        } catch (_: DataAccessException) {
            Failure(PostFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchPostByIdHashtag (
    private val selectPostByIdHashtag: (Int) -> List<Post>,
) : (Int) -> Result4k<List<Post>, PostFetchingError> {

    override operator fun invoke(hashtagId: Int): Result4k<List<Post>, PostFetchingError> =
        try {
            Success(selectPostByIdHashtag(hashtagId))
        } catch (_: DataAccessException) {
            Failure(PostFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchNNewPosts(
    private val selectNNewPosts: (Int) -> List<Post>,
) : (Int) -> Result4k<List<Post>, PostFetchingError> {

    override operator fun invoke(countN: Int): Result4k<List<Post>, PostFetchingError> =
        try {
            Success(selectNNewPosts(countN))
        } catch (_: DataAccessException) {
            Failure(PostFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchPostsByAuthorId(
    private val selectPostsByAuthorId: (Int) -> List<Post>,
) : (Int) -> Result4k<List<Post>, PostFetchingError> {

    override operator fun invoke(authorId: Int): Result4k<List<Post>, PostFetchingError> =
        try {
            Success(selectPostsByAuthorId(authorId))
        } catch (_: DataAccessException) {
            Failure(PostFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchPostsByModeratorId(
    private val selectPostsByModeratorId: (Int) -> List<Post>,
) : (Int) -> Result4k<List<Post>, PostFetchingError> {

    override operator fun invoke(moderatorId: Int): Result4k<List<Post>, PostFetchingError> =
        try {
            Success(selectPostsByModeratorId(moderatorId))
        } catch (_: DataAccessException) {
            Failure(PostFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchPostByStatus(
    private val selectPostByStatus: (Status) -> List<Post>,
) : (Status) -> Result4k<List<Post>, PostFetchingError> {

    override operator fun invoke(status: Status): Result4k<List<Post>, PostFetchingError> =
        try {
            Success(selectPostByStatus(status))
        } catch (_: DataAccessException) {
            Failure(PostFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchPostsByTimeInterval(
    private val selectPostsByTimeInterval: (ZonedDateTime, ZonedDateTime) -> List<Post>,
) : (ZonedDateTime, ZonedDateTime) -> Result4k<List<Post>, PostFetchingError> {

    override operator fun invoke(
        startDate: ZonedDateTime,
        endDate: ZonedDateTime,
    ): Result4k<List<Post>, PostFetchingError> =
        try {
            Success(selectPostsByTimeInterval(startDate, endDate))
        } catch (_: DataAccessException) {
            Failure(PostFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchAllPosts(
    private val selectAllPosts: () -> List<Post>,
) : () -> Result4k<List<Post>, PostFetchingError> {

    override operator fun invoke(): Result4k<List<Post>, PostFetchingError> =
        try {
            Success(selectAllPosts())
        } catch (_: DataAccessException) {
            Failure(PostFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class PostFetchingError {
    UNKNOWN_DATABASE_ERROR,
    NO_SUCH_POST,
}
