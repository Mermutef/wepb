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
import ru.yarsu.db.validPostDate2
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

class UpdateCommentTest : TestcontainerSpec({ context ->
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
    lateinit var insertedComment: Comment

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

        insertedComment =
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

    test("Comments content can be changed") {
        val newContent = "${validCommentContent}2"
        val updateComment = commentOperations
            .updateContent(insertedComment.id, newContent, validPostDate2)

        if (updateComment != null) {
            updateComment.content shouldBe newContent
            updateComment.lastModifiedDate shouldBe validPostDate2
        }
    }

    test("Comment can be hide") {
        val updateComment = commentOperations
            .hideComment(insertedComment.id)

        if (updateComment != null) {
            updateComment.isHidden shouldBe true
            updateComment.lastModifiedDate shouldBe validPostDate1
        }
    }

    test("Comment can be publish") {
        commentOperations
            .hideComment(insertedComment.id)
        val updateComment = commentOperations
            .publishComment(insertedComment.id)

        if (updateComment != null) {
            updateComment.isHidden shouldBe false
            updateComment.lastModifiedDate shouldBe validPostDate1
        }
    }
})