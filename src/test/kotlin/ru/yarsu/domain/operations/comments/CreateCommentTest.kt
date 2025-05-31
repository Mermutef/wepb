package ru.yarsu.domain.operations.comments

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.db.validCommentContent
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Comment
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.validEmail
import ru.yarsu.domain.operations.validHashtagTitle
import ru.yarsu.domain.operations.validLogin
import ru.yarsu.domain.operations.validName
import ru.yarsu.domain.operations.validPass
import ru.yarsu.domain.operations.validPhoneNumber
import ru.yarsu.domain.operations.validPostContent
import ru.yarsu.domain.operations.validPostDate1
import ru.yarsu.domain.operations.validPostPreview
import ru.yarsu.domain.operations.validPostTitle
import ru.yarsu.domain.operations.validUserSurname
import ru.yarsu.domain.operations.validVKLink

class CreateCommentTest : FunSpec({
    val validHashtag = Hashtag(1, validHashtagTitle)
    val validWriter = User(
        1,
        validName,
        validUserSurname,
        validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.WRITER
    )
    val validModerator = User(
        2,
        validName,
        validUserSurname,
        "${validLogin}2",
        "$2{validEmail}",
        "79111111111",
        validPass,
        validVKLink,
        Role.MODERATOR
    )
    val users = listOf(validWriter, validModerator)
    val validPost = Post(
        1,
        validPostTitle,
        validPostPreview,
        validPostContent,
        validHashtag.id,
        validPostDate1,
        validPostDate1,
        validPostDate1,
        validWriter.id,
        validModerator.id,
        Status.DRAFT
    )
    val posts = listOf(validPost)
    val comments = mutableListOf<Comment>()

    beforeEach {
        comments.clear()
    }

    val insertCommentMock: (
        content: String,
        authorId: Int,
        postId: Int,
    ) ->
    Comment? = { content, authorId, postId ->
        val comment =
            Comment(
                id = comments.size + 1,
                content = content,
                authorId = authorId,
                postId = postId,
                creationDate = validPostDate1,
                lastModifiedDate = validPostDate1,
                isHidden = false
            )
        comments.add(comment)
        comment
    }

    val fetchUserByIdMock: (Int) -> User? = { userId ->
        users.firstOrNull { it.id == userId }
    }

    val fetchPostByIdMock: (Int) -> Post? = { postId ->
        posts.firstOrNull { it.id == postId }
    }

    val insertCommentNullMock: (
        content: String,
        authorId: Int,
        postId: Int,
    ) -> Comment? = { _, _, _ -> null }

    val createComment = CreateComment(
        insertCommentMock,
        fetchUserByIdMock,
        fetchPostByIdMock
    )

    val createCommentNull = CreateComment(
        insertCommentNullMock,
        fetchUserByIdMock,
        fetchPostByIdMock
    )

    test("Valid comment can be inserted") {
        createComment(
            validCommentContent,
            validWriter.id,
            validPost.id
        ).shouldBeSuccess()
    }

    listOf(
        "",
        "    ",
        "a".repeat(Comment.MAX_CONTENT_LENGTH + 1),
    ).forEach { invalidContent ->
        test("Comment with invalid content should not be inserted ($invalidContent)") {
            createComment(
                invalidContent,
                validWriter.id,
                validPost.id
            ).shouldBeFailure(CommentCreationError.INVALID_COMMENT_DATA)
        }
    }

    test("comment with invalid author id can not be inserted") {
        createComment(
            validCommentContent,
            Int.MIN_VALUE,
            validPost.id
        ).shouldBeFailure(CommentCreationError.AUTHOR_NOT_EXISTS)
    }

    test("comment with invalid post id can not be inserted") {
        createComment(
            validCommentContent,
            validWriter.id,
            Int.MIN_VALUE
        ).shouldBeFailure(CommentCreationError.POST_NOT_EXISTS)
    }

    test("Unknown db error test for CreateComment") {
        createCommentNull(
            validCommentContent,
            validWriter.id,
            validPost.id
        ).shouldBeFailure(CommentCreationError.UNKNOWN_DATABASE_ERROR)
    }
})
