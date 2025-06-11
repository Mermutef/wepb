package ru.yarsu.domain.operations.comments

import dev.forkhandles.result4k.Result4k
import ru.yarsu.domain.dependencies.CommentsDatabase
import ru.yarsu.domain.dependencies.PostsDatabase
import ru.yarsu.domain.dependencies.UsersDatabase
import ru.yarsu.domain.models.Comment

class CommentOperationsHolder(
    private val commentsDatabase: CommentsDatabase,
    private val postsDatabase: PostsDatabase,
    private val usersDatabase: UsersDatabase,
) {
    val fetchCommentById: (Int) -> Result4k<Comment, CommentFetchingError> =
        FetchCommentById(selectCommentById = commentsDatabase::selectCommentById)

    val fetchPublishedCommentsInPost: (Int) -> Result4k<List<Comment>, CommentFetchingError> =
        FetchPublishedCommentsInPost(
            selectPublishedCommentsInPost = commentsDatabase::selectPublishedCommentsInPost
        )

    val fetchHiddenCommentsInPost: (Int) -> Result4k<List<Comment>, CommentFetchingError> =
        FetchHiddenCommentsInPost(
            selectHiddenCommentsInPost =
                commentsDatabase::selectHiddenCommentsInPost
        )

    val fetchHiddenCommentOfUserInPost: (Int, Int) -> Result4k<List<Comment>, CommentFetchingError> =
        FetchHiddenCommentsOfUserInPost(
            selectHiddenCommentsOfUserInPost =
                commentsDatabase::selectHiddenCommentsOfUserInPost
        )

    val createComment: (
        content: String,
        authorId: Int,
        postId: Int,
    ) -> Result4k<Comment, CommentCreationError> =
        CreateComment(
            insertComment = {
                    content,
                    authorId,
                    postId,
                ->
                commentsDatabase.insertComment(
                    content = content,
                    authorId = authorId,
                    postId = postId
                )
            },
            selectUserById = usersDatabase::selectUserByID,
            selectPostById = postsDatabase::selectPostByID
        )

    val makeHidden: (Comment) -> Result4k<Comment, FieldInCommentChangingError> =
        ChangeVisibilityComment(changeVisibility = commentsDatabase::hideComment)

    val makePublished: (Comment) -> Result4k<Comment, FieldInCommentChangingError> =
        ChangeVisibilityComment(changeVisibility = commentsDatabase::publishComment)

    val changeContent: (Comment, String) -> Result4k<Comment, FieldInCommentChangingError> =
        ChangeContentInComment(changeContent = commentsDatabase::updateContent)
}
