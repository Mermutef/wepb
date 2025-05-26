package ru.yarsu.domain.operations.posts

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.PostValidationResult
import ru.yarsu.domain.models.Status
import ru.yarsu.domain.models.User
import java.time.ZonedDateTime

class CreatePost(
    private val insertPost: (
        title: String,
        preview: String,
        content: String,
        hashtagId: Int,
        eventDate: ZonedDateTime?,
        authorId: Int,
        moderatorId: Int?,
        status: Status,
    ) -> Post?,
    private val selectHashtagById: (id: Int) -> Hashtag?,
    private val selectUserById: (id: Int) -> User?,
    private val selectMediaByName: (name: String) -> MediaFile?,
) : (String, String, String, Int, ZonedDateTime?, Int, Int?, Status)
    -> Result4k<Post, PostCreationError> {
    override operator fun invoke(
        title: String,
        preview: String,
        content: String,
        hashtagId: Int,
        eventDate: ZonedDateTime?,
        authorId: Int,
        moderatorId: Int?,
        status: Status,
    ): Result4k<Post, PostCreationError> =
        when {
            Post.validatePostData(title, preview, content) != PostValidationResult.ALL_OK ->
                Failure(PostCreationError.INVALID_POST_DATA)
            hashtagNotExists(hashtagId) ->
                Failure(PostCreationError.HASHTAG_NOT_EXISTS)
            userNotExists(authorId) ->
                Failure(PostCreationError.AUTHOR_NOT_EXISTS)
            moderatorId != null && userNotExists(moderatorId) ->
                Failure(PostCreationError.MODERATOR_NOT_EXISTS)
            mediaNotExists(preview) ->
                Failure(PostCreationError.MEDIA_NOT_EXISTS)
            else ->
                when (
                    val newPost = insertPost(
                        title,
                        preview,
                        content,
                        hashtagId,
                        eventDate,
                        authorId,
                        moderatorId,
                        status
                    )
                ) {
                    is Post -> Success(newPost)
                    else -> Failure(PostCreationError.UNKNOWN_DATABASE_ERROR)
                }
        }

    private fun hashtagNotExists(hashtagId: Int): Boolean =
        when (selectHashtagById(hashtagId)) {
            is Hashtag -> false
            else -> true
        }

    private fun userNotExists(userId: Int?): Boolean =
        when (userId?.let { selectUserById(it) }) {
            is User -> false
            else -> true
        }

    private fun mediaNotExists(name: String): Boolean =
        when (selectMediaByName(name)) {
            is MediaFile -> false
            else -> true
        }
}

enum class PostCreationError {
    HASHTAG_NOT_EXISTS,
    AUTHOR_NOT_EXISTS,
    MEDIA_NOT_EXISTS,
    MODERATOR_NOT_EXISTS,
    INVALID_POST_DATA,
    UNKNOWN_DATABASE_ERROR,
}
