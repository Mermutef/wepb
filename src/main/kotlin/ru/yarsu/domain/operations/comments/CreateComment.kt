package ru.yarsu.domain.operations.comments

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import ru.yarsu.domain.models.Comment
import ru.yarsu.domain.models.CommentValidationResult

import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User

class CreateComment(
    private val insertComment: (content: String, authorId: Int, postId: Int) -> Comment?,
    private val selectUserById: (userId: Int) -> User?,
    private val selectPostById: (postId: Int) -> Post?
) : (String, Int, Int) -> Result4k<Comment, CommentCreationError> {
    override operator fun invoke(content: String,
                                 authorId: Int,
                                 postId: Int): Result4k<Comment, CommentCreationError> =
        when {
            Comment.validateCommentData(content) != CommentValidationResult.ALL_OK ->
                Failure(CommentCreationError.INVALID_COMMENT_DATA)
            authorNotExists(authorId) ->
                Failure(CommentCreationError.AUTHOR_NOT_EXISTS)
            postNotExists(postId) ->
                Failure(CommentCreationError.POST_NOT_EXISTS)
            else ->
                when (
                    val newComment = insertComment(content, authorId, postId)
                ) {
                    is Comment -> Success(newComment)
                    else -> Failure(CommentCreationError.UNKNOWN_DATABASE_ERROR)
                }
        }

    private fun authorNotExists(authorId: Int): Boolean =
        when (selectUserById(authorId)) {
            is User -> false
            else -> true
        }

    private fun postNotExists(postId: Int): Boolean =
        when (selectPostById(postId)) {
            is Post -> false
            else -> true
        }
}

enum class CommentCreationError {
    AUTHOR_NOT_EXISTS,
    POST_NOT_EXISTS,
    INVALID_COMMENT_DATA,
    UNKNOWN_DATABASE_ERROR,
}