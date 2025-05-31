package ru.yarsu.db.comments

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.comment.CommentOperations
import ru.yarsu.db.hashtag.HashtagsOperations
import ru.yarsu.db.media.MediaOperations
import ru.yarsu.db.posts.PostsOperations
import ru.yarsu.db.users.UserOperations
import ru.yarsu.db.validCommentContent
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
import ru.yarsu.domain.models.Comment
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class InsertCommentTest : TestcontainerSpec({ context ->
    val hashtagOperations = HashtagsOperations(context)
    val postOperations = PostsOperations(context)
    val userOperations = UserOperations(context)
    val mediaOperations = MediaOperations(context)
    val commentOperations = CommentOperations(context)

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

    test("Valid comment can be inserted") {
        commentOperations
            .insertComment(
                validCommentContent,
                insertedModerator.id,
                insertedPost.id,
                validPostDate1,
                validPostDate1,
                false,
            ).shouldNotBeNull()
    }

    test("Valid comment insertion should return this hashtag") {
        val insertedComment =
            commentOperations
                .insertComment(
                    validCommentContent,
                    insertedModerator.id,
                    insertedPost.id,
                    validPostDate1,
                    validPostDate1,
                    false,
                ).shouldNotBeNull()

        insertedComment.content.shouldBe(validCommentContent)
        insertedComment.authorId.shouldBe(insertedModerator.id)
        insertedComment.postId.shouldBe(insertedPost.id)
        insertedComment.creationDate.shouldBe(validPostDate1)
        insertedComment.lastModifiedDate.shouldBe(validPostDate1)
        insertedComment.isHidden.shouldBe(false)
    }

    test("Valid comment with long content can be inserted") {
        val insertedComment =
            commentOperations
                .insertComment(
                    "a".repeat(Comment.MAX_CONTENT_LENGTH),
                    insertedModerator.id,
                    insertedPost.id,
                    validPostDate1,
                    validPostDate1,
                    false,
                ).shouldNotBeNull()

        insertedComment.content.shouldBe("a".repeat(Comment.MAX_CONTENT_LENGTH))
        insertedComment.authorId.shouldBe(insertedModerator.id)
        insertedComment.postId.shouldBe(insertedPost.id)
        insertedComment.creationDate.shouldBe(validPostDate1)
        insertedComment.lastModifiedDate.shouldBe(validPostDate1)
        insertedComment.isHidden.shouldBe(false)
    }
})
