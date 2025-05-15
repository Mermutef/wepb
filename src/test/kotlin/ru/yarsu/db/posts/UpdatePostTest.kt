package ru.yarsu.db.posts

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.hashtag.HashtagsOperations
import ru.yarsu.db.media.MediaOperations
import ru.yarsu.db.users.UserOperations
import ru.yarsu.db.validEmail
import ru.yarsu.db.validHashtagTitle
import ru.yarsu.db.validLogin
import ru.yarsu.db.validName
import ru.yarsu.db.validPass
import ru.yarsu.db.validPhoneNumber
import ru.yarsu.db.validPostContent
import ru.yarsu.db.validPostDate1
import ru.yarsu.db.validPostDate2
import ru.yarsu.db.validPostPreview
import ru.yarsu.db.validPostTitle
import ru.yarsu.db.validSecondPhoneNumber
import ru.yarsu.db.validUserSurname
import ru.yarsu.db.validVKLink
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import java.time.LocalDateTime
import java.time.ZonedDateTime

class UpdatePostTest: TestcontainerSpec({ context ->
val hashtagOperations = HashtagsOperations(context)
    val postOperations = PostsOperations(context)
    val userOperations = UserOperations(context)
    val mediaOperations = MediaOperations(context)

    lateinit var insertedHashtag: Hashtag
    lateinit var insertedWriter: User
    lateinit var insertedModerator: User
    lateinit var insertedMedia: MediaFile
    lateinit var insertedPost: Post

    beforeEach {
        insertedHashtag =
            hashtagOperations
                .insertHashtag(
                    validHashtagTitle,
                ).shouldNotBeNull()

        insertedWriter =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber,
                    validPass,
                    validVKLink,
                    Role.WRITER,
                )
                .shouldNotBeNull()

        insertedModerator =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    "${validLogin}2",
                    "${validEmail}2",
                    validSecondPhoneNumber,
                    validPass,
                    validVKLink,
                    Role.WRITER,
                )
                .shouldNotBeNull()

        insertedMedia =
            mediaOperations
                .insertMedia(
                    filename = validPostPreview,
                    authorId = insertedWriter.id,
                    mediaType = MediaType.VIDEO,
                    content = "Valid content".toByteArray(),
                    birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
                ).shouldNotBeNull()

        insertedPost =
            postOperations
                .insertPost(
                    validPostTitle,
                    validPostPreview,
                    validPostContent,
                    insertedHashtag.id,
                    validPostDate1,
                    validPostDate1,
                    validPostDate1,
                    insertedWriter.id,
                    insertedModerator.id,
                    Status.DRAFT
                ).shouldNotBeNull()
    }

    test("Post title can be changed") {
        val newTitle = "${validPostTitle}2"
        postOperations
            .updateTitle(insertedPost.id, newTitle, ZonedDateTime.now())
            .shouldNotBeNull().title shouldBe newTitle
    }

    test("Post preview can be changed") {
        mediaOperations
            .insertMedia(
                filename = "${validPostPreview}a",
                authorId = insertedWriter.id,
                mediaType = MediaType.VIDEO,
                content = "Valid content".toByteArray(),
                birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
                isTemporary = false
            ).shouldNotBeNull()

        val newPreview = "${validPostPreview}a"
        postOperations
            .updatePreview(insertedPost.id, newPreview, ZonedDateTime.now())
            .shouldNotBeNull().preview shouldBe newPreview
    }

    test("Post content can be changed") {
        val newContent = "${validPostContent}a"
        postOperations
            .updateContent(insertedPost.id, newContent, ZonedDateTime.now())
            .shouldNotBeNull().content shouldBe newContent
    }

    test("Post hashtag can be changed") {
        val newInsertedHashtag = hashtagOperations
            .insertHashtag(
                "${validHashtagTitle}2",
            ).shouldNotBeNull()

        val newHashtagId = newInsertedHashtag.id
        postOperations
            .updateHashtagId(insertedPost.id, newHashtagId, ZonedDateTime.now())
            .shouldNotBeNull().hashtagId shouldBe newHashtagId
    }

    test("Post event date can be changed") {
        postOperations
            .updateEventDate(insertedPost.id, validPostDate2, ZonedDateTime.now())
            .shouldNotBeNull().eventDate shouldBe validPostDate2
    }

    test("Post author id can be changed") {
        postOperations
            .updateAuthorId(insertedPost.id, insertedModerator.id, ZonedDateTime.now())
            .shouldNotBeNull().authorId shouldBe insertedModerator.id
    }

    test("Post moderator id can be changed") {
        postOperations
            .updateModeratorId(insertedPost.id, insertedWriter.id, ZonedDateTime.now())
            .shouldNotBeNull().authorId shouldBe insertedWriter.id
    }

    test("Post status can be changed") {
        postOperations
            .updateStatus(insertedPost, Status.HIDDEN, ZonedDateTime.now())
            .shouldNotBeNull().status shouldBe Status.HIDDEN

        postOperations
            .updateStatus(insertedPost, Status.MODERATION, ZonedDateTime.now())
            .shouldNotBeNull().status shouldBe Status.MODERATION

        postOperations
            .updateStatus(insertedPost, Status.PUBLISHED, ZonedDateTime.now())
            .shouldNotBeNull().status shouldBe Status.PUBLISHED

        postOperations
            .updateStatus(insertedPost, Status.DRAFT, ZonedDateTime.now())
            .shouldNotBeNull().status shouldBe Status.DRAFT
    }
})