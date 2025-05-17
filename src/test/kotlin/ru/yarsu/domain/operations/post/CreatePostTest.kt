package ru.yarsu.domain.operations.post

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.posts.CreatePosts
import ru.yarsu.domain.operations.posts.PostCreationError
import ru.yarsu.domain.operations.users.UserCreationError
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
import java.time.ZonedDateTime

class CreatePostTest : FunSpec({
    val posts = mutableListOf<Post>()
    val validHashtag = Hashtag(1, validHashtagTitle)
    val hashtags = listOf(validHashtag)
    val validWriter = User(1,
        validName,
        validUserSurname,
        validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.WRITER
        )
    val validModerator = User(2,
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

    beforeEach {
        posts.clear()
    }

    val insertPostMock: (
        title: String,
        preview: String,
        content: String,
        hashtagId: Int,
        eventDate: ZonedDateTime?,
        creationDate: ZonedDateTime,
        lastModifiedDate: ZonedDateTime,
        authorId: Int,
        moderatorId: Int?,
        status: Status,
    ) ->
    Post? = { title,
              preview,
              content,
              hashtagId,
              eventDate,
              creationData,
              lastModifiedDate,
              authorId,
              moderatorId,
              status ->
        val post =
            Post(
                id = posts.size + 1,
                title,
                preview,
                content,
                hashtagId,
                eventDate,
                creationData,
                lastModifiedDate,
                authorId,
                moderatorId,
                status
            )
        posts.add(post)
        post
    }

    val fetchHashtagByIdMock: (Int) -> Hashtag? = { hashtagId ->
        hashtags.firstOrNull { it.id == hashtagId }
    }

    val fetchUserByIdMock: (Int) -> User? = { userId ->
        users.firstOrNull { it.id == userId }
    }

    val insertPostNullMock: (
        title: String,
        preview: String,
        content: String,
        hashtagId: Int,
        eventDate: ZonedDateTime?,
        creationDate: ZonedDateTime,
        lastModifiedDate: ZonedDateTime,
        authorId: Int,
        moderatorId: Int?,
        status: Status,
    ) -> Post? = { _, _, _, _, _, _, _, _, _, _ -> null }

    val createPost = CreatePosts(
        insertPostMock,
        fetchHashtagByIdMock,
        fetchUserByIdMock
    )

    val createPostNull = CreatePosts(
        insertPostNullMock,
        fetchHashtagByIdMock,
        fetchUserByIdMock
    )

    test("Valid user can be inserted") {
        createPost(
            validPostTitle,
            validPostPreview,
            validPostContent,
            validHashtag.id,
            validPostDate1,
            validWriter.id,
            validModerator.id,
            Status.DRAFT
        )
            .shouldBeSuccess()
    }

    Status
        .entries
        .forEach { status ->
            test("All valid status can be inserted ($status)") {
                createPost(
                    validPostTitle,
                    validPostPreview,
                    validPostContent,
                    validHashtag.id,
                    validPostDate1,
                    validWriter.id,
                    validModerator.id,
                    status
                ).shouldBeSuccess()
            }
        }

    listOf(
        "",
        "a".repeat(Post.MAX_TITLE_LENGTH + 1),
    ).forEach { invalidTitle ->
        test("Post with invalid title should not be inserted ($invalidTitle)") {
            createPost(
                invalidTitle,
                validPostPreview,
                validPostContent,
                validHashtag.id,
                validPostDate1,
                validWriter.id,
                validModerator.id,
                Status.DRAFT
            ).shouldBeFailure(PostCreationError.INVALID_POST_DATA)
        }
    }

    listOf(
        "",
        "a".repeat(Post.MAX_PREVIEW_LENGTH + 1),
    ).forEach { invalidPreview ->
        test("Post with invalid preview should not be inserted ($invalidPreview)") {
            createPost(
                validPostTitle,
                invalidPreview,
                validPostContent,
                validHashtag.id,
                validPostDate1,
                validWriter.id,
                validModerator.id,
                Status.DRAFT
            ).shouldBeFailure(PostCreationError.INVALID_POST_DATA)
        }
    }

    listOf(
        ""
    ).forEach { invalidContent ->
        test("Post with invalid content should not be inserted ($invalidContent)") {
            createPost(
                validPostTitle,
                validPostPreview,
                invalidContent,
                validHashtag.id,
                validPostDate1,
                validWriter.id,
                validModerator.id,
                Status.DRAFT
            ).shouldBeFailure(PostCreationError.INVALID_POST_DATA)
        }
    }

    test("Post with invalid author id should not be inserted") {
        createPost(
            validPostTitle,
            validPostPreview,
            validPostContent,
            validHashtag.id,
            validPostDate1,
            Int.MIN_VALUE,
            validModerator.id,
            Status.DRAFT
        )
            .shouldBeFailure(PostCreationError.AUTHOR_NOT_EXISTS)
    }

    test("Post with invalid moderator id should not be inserted") {
        createPost(
            validPostTitle,
            validPostPreview,
            validPostContent,
            validHashtag.id,
            validPostDate1,
            validWriter.id,
            Int.MIN_VALUE,
            Status.DRAFT
        )
            .shouldBeFailure(PostCreationError.MODERATOR_NOT_EXISTS)
    }

    test("Post with invalid hashtag id should not be inserted") {
        createPost(
            validPostTitle,
            validPostPreview,
            validPostContent,
            Int.MIN_VALUE,
            validPostDate1,
            validWriter.id,
            validModerator.id,
            Status.DRAFT
        )
            .shouldBeFailure(PostCreationError.HASHTAG_NOT_EXISTS)
    }

    test("Unknown db error test for CreateUser") {
        createPostNull(
            validPostTitle,
            validPostPreview,
            validPostContent,
            validHashtag.id,
            validPostDate1,
            validWriter.id,
            validModerator.id,
            Status.DRAFT
        ).shouldBeFailure(PostCreationError.UNKNOWN_DATABASE_ERROR)
    }

    test("There can be user with null event date") {
        createPost(
            validPostTitle,
            validPostPreview,
            validPostContent,
            validHashtag.id,
            null,
            validWriter.id,
            validModerator.id,
            Status.DRAFT
        ).shouldBeSuccess()
    }

    test("There can be user with null moderator id") {
        createPost(
            validPostTitle,
            validPostPreview,
            validPostContent,
            validHashtag.id,
            validPostDate1,
            validWriter.id,
            null,
            Status.DRAFT
        ).shouldBeSuccess()
    }
})