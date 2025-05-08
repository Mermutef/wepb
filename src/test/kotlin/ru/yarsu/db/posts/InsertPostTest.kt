package ru.yarsu.db.posts

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.appConfiguredPasswordHasher
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

class InsertPostTest: TestcontainerSpec({ context ->
    val hashtagOperations = HashtagsOperations(context)
    val postOperations = PostsOperations(context)
    val userOperations = UserOperations(context)
    val mediaOperations = MediaOperations(context)

    lateinit var insertedHashtag: Hashtag
    lateinit var insertedWriter: User
    lateinit var insertedModerator: User
    lateinit var insertedMedia: MediaFile

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
    }

    test("Valid post can be inserted") {
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

    test("Valid post insertion should return this user") {
        val insertedPost =
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

        insertedPost.title.shouldBe(validPostTitle)
        insertedPost.preview.shouldBe(validPostPreview)
        insertedPost.content.shouldBe(validPostContent)
        insertedPost.hashtagId.shouldBe(insertedHashtag.id)
        insertedPost.eventDate.shouldBe(validPostDate1)
        insertedPost.creationDate.shouldBe(validPostDate1)
        insertedPost.lastModifiedDate.shouldBe(validPostDate1)
        insertedPost.authorId.shouldBe(insertedWriter.id)
        insertedPost.moderatorId.shouldBe(insertedModerator.id)
        insertedPost.status.shouldBe(Status.DRAFT)
    }

    test("Valid user with long title, preview can be inserted") {
        val insertedMediaWithLongName =
            mediaOperations
                .insertMedia(
                    filename = "a".repeat(Post.MAX_PREVIEW_LENGTH),
                    authorId = insertedWriter.id,
                    mediaType = MediaType.VIDEO,
                    content = "Valid content".toByteArray(),
                    birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
                ).shouldNotBeNull()

        val insertedPost =
            postOperations
                .insertPost(
                    "a".repeat(Post.MAX_TITLE_LENGTH),
                    "a".repeat(Post.MAX_PREVIEW_LENGTH),
                    validPostContent,
                    insertedHashtag.id,
                    validPostDate1,
                    validPostDate1,
                    validPostDate1,
                    insertedWriter.id,
                    insertedModerator.id,
                    Status.DRAFT
                ).shouldNotBeNull()

        insertedPost.title.shouldBe("a".repeat(Post.MAX_TITLE_LENGTH))
        insertedPost.preview.shouldBe("a".repeat(Post.MAX_PREVIEW_LENGTH))
        insertedPost.content.shouldBe(validPostContent)
        insertedPost.hashtagId.shouldBe(insertedHashtag.id)
        insertedPost.eventDate.shouldBe(validPostDate1)
        insertedPost.creationDate.shouldBe(validPostDate1)
        insertedPost.lastModifiedDate.shouldBe(validPostDate1)
        insertedPost.authorId.shouldBe(insertedWriter.id)
        insertedPost.moderatorId.shouldBe(insertedModerator.id)
        insertedPost.status.shouldBe(Status.DRAFT)
    }

    test("Valid user with null eventDate, moderatorId can be inserted") {
        val insertedPost =
            postOperations
                .insertPost(
                    validPostTitle,
                    validPostPreview,
                    validPostContent,
                    insertedHashtag.id,
                    null,
                    validPostDate1,
                    validPostDate1,
                    insertedWriter.id,
                    null,
                    Status.DRAFT
                ).shouldNotBeNull()

        insertedPost.title.shouldBe(validPostTitle)
        insertedPost.preview.shouldBe(validPostPreview)
        insertedPost.content.shouldBe(validPostContent)
        insertedPost.hashtagId.shouldBe(insertedHashtag.id)
        insertedPost.eventDate.shouldBe(null)
        insertedPost.creationDate.shouldBe(validPostDate1)
        insertedPost.lastModifiedDate.shouldBe(validPostDate1)
        insertedPost.authorId.shouldBe(insertedWriter.id)
        insertedPost.moderatorId.shouldBe(null)
        insertedPost.status.shouldBe(Status.DRAFT)
    }

    test("Valid user with status = DRAFT can be inserted") {
        val insertedPost =
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

        insertedPost.title.shouldBe(validPostTitle)
        insertedPost.preview.shouldBe(validPostPreview)
        insertedPost.content.shouldBe(validPostContent)
        insertedPost.hashtagId.shouldBe(insertedHashtag.id)
        insertedPost.eventDate.shouldBe(validPostDate1)
        insertedPost.creationDate.shouldBe(validPostDate1)
        insertedPost.lastModifiedDate.shouldBe(validPostDate1)
        insertedPost.authorId.shouldBe(insertedWriter.id)
        insertedPost.moderatorId.shouldBe(insertedModerator.id)
        insertedPost.status.shouldBe(Status.DRAFT)
    }

    test("Valid user with status = MODERATION can be inserted") {
        val insertedPost =
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
                    Status.MODERATION
                ).shouldNotBeNull()

        insertedPost.title.shouldBe(validPostTitle)
        insertedPost.preview.shouldBe(validPostPreview)
        insertedPost.content.shouldBe(validPostContent)
        insertedPost.hashtagId.shouldBe(insertedHashtag.id)
        insertedPost.eventDate.shouldBe(validPostDate1)
        insertedPost.creationDate.shouldBe(validPostDate1)
        insertedPost.lastModifiedDate.shouldBe(validPostDate1)
        insertedPost.authorId.shouldBe(insertedWriter.id)
        insertedPost.moderatorId.shouldBe(insertedModerator.id)
        insertedPost.status.shouldBe(Status.MODERATION)
    }

    test("Valid user with status = HIDDEN can be inserted") {
        val insertedPost =
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
                    Status.HIDDEN
                ).shouldNotBeNull()

        insertedPost.title.shouldBe(validPostTitle)
        insertedPost.preview.shouldBe(validPostPreview)
        insertedPost.content.shouldBe(validPostContent)
        insertedPost.hashtagId.shouldBe(insertedHashtag.id)
        insertedPost.eventDate.shouldBe(validPostDate1)
        insertedPost.creationDate.shouldBe(validPostDate1)
        insertedPost.lastModifiedDate.shouldBe(validPostDate1)
        insertedPost.authorId.shouldBe(insertedWriter.id)
        insertedPost.moderatorId.shouldBe(insertedModerator.id)
        insertedPost.status.shouldBe(Status.HIDDEN)
    }

    test("Valid user with status = PUBLISHED can be inserted") {
        val insertedPost =
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
                    Status.PUBLISHED
                ).shouldNotBeNull()

        insertedPost.title.shouldBe(validPostTitle)
        insertedPost.preview.shouldBe(validPostPreview)
        insertedPost.content.shouldBe(validPostContent)
        insertedPost.hashtagId.shouldBe(insertedHashtag.id)
        insertedPost.eventDate.shouldBe(validPostDate1)
        insertedPost.creationDate.shouldBe(validPostDate1)
        insertedPost.lastModifiedDate.shouldBe(validPostDate1)
        insertedPost.authorId.shouldBe(insertedWriter.id)
        insertedPost.moderatorId.shouldBe(insertedModerator.id)
        insertedPost.status.shouldBe(Status.PUBLISHED)
    }
})