package ru.yarsu.domain.operations.posts

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.PostValidationResult
import java.time.ZonedDateTime

class CreatePosts(
    private val insertPost: (
        title: String,
        preview: String,
        content: String,
        hashtagId: Int,
        eventDate: ZonedDateTime?,
        creationDate: ZonedDateTime,
        lastModifiedDate: ZonedDateTime,
        authorId: Int,
        moderatorId: Int,
        status: Status,
    ) -> Post?,
    private val selectHashtagById: (id: Int) -> Hashtag?,
) : (String, String, String, Int, ZonedDateTime?, ZonedDateTime, ZonedDateTime, Int, Int, Status)
    -> Result4k<Post, PostCreationError> {
    override operator fun invoke(
        title: String,
        preview: String,
        content: String,
        hashtagId: Int,
        eventDate: ZonedDateTime?,
        creationDate: ZonedDateTime,
        lastModifiedDate: ZonedDateTime,
        authorId: Int,
        moderatorId: Int,
        status: Status,
    ): Result4k<Post, PostCreationError> =
        when {
            Post.validatePostData(title, preview, content) != PostValidationResult.ALL_OK ->
                Failure(PostCreationError.INVALID_POST_DATA)
            hashtagNotExists(hashtagId) ->
                Failure(PostCreationError.HASHTAG_NOT_EXISTS)
            else ->
                when (
                    val newPost = insertPost(
                        title,
                        preview,
                        content,
                        hashtagId,
                        eventDate,
                        creationDate,
                        lastModifiedDate,
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
}

enum class PostCreationError {
    HASHTAG_NOT_EXISTS,
    INVALID_POST_DATA,
    UNKNOWN_DATABASE_ERROR,
}
