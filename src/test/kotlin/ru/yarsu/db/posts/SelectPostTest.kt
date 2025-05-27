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
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class SelectPostTest : TestcontainerSpec({ context ->
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

    test("There is only post by default") {
        postOperations
            .selectAllPosts()
            .shouldNotBeNull()
            .size
            .shouldBe(1)
    }

    test("two posts were added") {

        // already added one

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

        postOperations
            .selectAllPosts()
            .shouldNotBeNull()
            .size
            .shouldBe(2)
    }

    test("Post can be fetched by valid ID") {
        val fetchedPost =
            postOperations
                .selectPostByID(insertedPost.id)
                .shouldNotBeNull()

        fetchedPost.title.shouldBe(validPostTitle)
        fetchedPost.preview.shouldBe(validPostPreview)
        fetchedPost.content.shouldBe(validPostContent)
        fetchedPost.hashtagId.shouldBe(insertedHashtag.id)
        fetchedPost.eventDate.shouldBe(validPostDate1)
        fetchedPost.creationDate.shouldBe(validPostDate1)
        fetchedPost.lastModifiedDate.shouldBe(validPostDate1)
        fetchedPost.authorId.shouldBe(insertedWriter.id)
        fetchedPost.moderatorId.shouldBe(insertedModerator.id)
        fetchedPost.status.shouldBe(Status.DRAFT)
    }

    test("Post can be fetched by valid hashtagId") {
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

        val secondHashtag =
            hashtagOperations
                .insertHashtag(
                    "${validHashtagTitle}2",
                ).shouldNotBeNull()

        postOperations
            .insertPost(
                validPostTitle,
                validPostPreview,
                validPostContent,
                secondHashtag.id,
                validPostDate1,
                validPostDate1,
                validPostDate1,
                insertedWriter.id,
                insertedModerator.id,
                Status.DRAFT
            ).shouldNotBeNull()

        postOperations
            .selectAllPosts()
            .shouldNotBeNull()
            .size
            .shouldBe(3)

        val fetchedPosts =
            postOperations
                .selectPostsByHashtagId(insertedHashtag.id)
                .shouldNotBeNull()

        fetchedPosts.size.shouldBe(2)

        postOperations
            .selectPostsByHashtagId(secondHashtag.id)
            .shouldNotBeNull()
            .size
            .shouldBe(1)

        val fetchedPost = fetchedPosts[0]

        fetchedPost.title.shouldBe(validPostTitle)
        fetchedPost.preview.shouldBe(validPostPreview)
        fetchedPost.content.shouldBe(validPostContent)
        fetchedPost.hashtagId.shouldBe(insertedHashtag.id)
        fetchedPost.eventDate.shouldBe(validPostDate1)
        fetchedPost.creationDate.shouldBe(validPostDate1)
        fetchedPost.lastModifiedDate.shouldBe(validPostDate1)
        fetchedPost.authorId.shouldBe(insertedWriter.id)
        fetchedPost.moderatorId.shouldBe(insertedModerator.id)
        fetchedPost.status.shouldBe(Status.DRAFT)
    }

    test("N new posts can be fetched (n < count, n = count, n > count)") {
        for (i in 1..3)
            postOperations
                .insertPost(
                    validPostTitle,
                    validPostPreview,
                    validPostContent,
                    insertedHashtag.id,
                    validPostDate1,
                    validPostDate2,
                    validPostDate2,
                    insertedWriter.id,
                    insertedModerator.id,
                    Status.DRAFT
                ).shouldNotBeNull()
        val fetchedPosts = postOperations
            .selectNNewestPosts(3)
            .shouldNotBeNull()

        fetchedPosts.size.shouldBe(3)
        for (i in 0..2)
            fetchedPosts[i].creationDate.shouldBe(validPostDate2)

        postOperations
            .selectNNewestPosts(4)
            .shouldNotBeNull()
            .size
            .shouldBe(4)

        postOperations
            .selectNNewestPosts(4000)
            .shouldNotBeNull()
            .size
            .shouldBe(4)
    }

    test("Post can be fetched by valid authorId") {
        for (i in 1..2)
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

        postOperations
            .insertPost(
                validPostTitle,
                validPostPreview,
                validPostContent,
                insertedHashtag.id,
                validPostDate1,
                validPostDate1,
                validPostDate1,
                insertedModerator.id,
                insertedWriter.id,
                Status.DRAFT
            ).shouldNotBeNull()

        val fetchedPosts = postOperations
            .selectPostsByAuthorId(insertedWriter.id)

        fetchedPosts.size.shouldBe(3)
        for (i in 0..2)
            fetchedPosts[i].authorId.shouldBe(insertedWriter.id)

        postOperations
            .selectPostsByAuthorId(insertedModerator.id)
            .size
            .shouldBe(1)
    }

    test("Post can be fetched by valid moderatorId") {
        for (i in 1..2)
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

        postOperations
            .insertPost(
                validPostTitle,
                validPostPreview,
                validPostContent,
                insertedHashtag.id,
                validPostDate1,
                validPostDate1,
                validPostDate1,
                insertedModerator.id,
                insertedWriter.id,
                Status.DRAFT
            ).shouldNotBeNull()

        val fetchedPosts = postOperations
            .selectPostsByModeratorId(insertedModerator.id)

        fetchedPosts.size.shouldBe(3)
        for (i in 0..2)
            fetchedPosts[i].moderatorId.shouldBe(insertedModerator.id)

        postOperations
            .selectPostsByModeratorId(insertedWriter.id)
            .size
            .shouldBe(1)
    }

    test("Post can be fetched by valid status") {
        for (i in 1..3)
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

        postOperations
            .insertPost(
                validPostTitle,
                validPostPreview,
                validPostContent,
                insertedHashtag.id,
                validPostDate1,
                validPostDate1,
                validPostDate1,
                insertedModerator.id,
                insertedWriter.id,
                Status.HIDDEN
            ).shouldNotBeNull()

        val fetchedPosts = postOperations
            .selectPostsByStatus(Status.MODERATION)

        fetchedPosts.size.shouldBe(3)
        for (i in 0..2)
            fetchedPosts[i].status.shouldBe(Status.MODERATION)

        postOperations
            .selectPostsByStatus(Status.HIDDEN)
            .size
            .shouldBe(1)
    }

    test("Post can be fetched by valid time interval") {
        for (i in 1..3)
            postOperations
                .insertPost(
                    validPostTitle,
                    validPostPreview,
                    validPostContent,
                    insertedHashtag.id,
                    validPostDate1,
                    validPostDate2,
                    validPostDate2,
                    insertedWriter.id,
                    insertedModerator.id,
                    Status.DRAFT
                ).shouldNotBeNull()

        val fetchedPosts = postOperations
            .selectPostsByTimeInterval(validPostDate2, validPostDate2)

        fetchedPosts.size.shouldBe(3)

        for (i in 0..2)
            fetchedPosts[i].creationDate.shouldBe(validPostDate2)

        val fetchedPosts2 = postOperations
            .selectPostsByTimeInterval(validPostDate1, validPostDate1)

        fetchedPosts2.size.shouldBe(1)

        fetchedPosts2[0].creationDate.shouldBe(validPostDate1)

        postOperations
            .selectPostsByTimeInterval(validPostDate1, validPostDate2)
            .size
            .shouldBe(4)
    }
})
