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

class SelectCommentTest : TestcontainerSpec({ context ->
    val hashtagOperations = HashtagsOperations(context)
    val postOperations = PostsOperations(context)
    val userOperations = UserOperations(context)
    val mediaOperations = MediaOperations(context)
    val commentOperations = CommentOperations(context)

    lateinit var insertedHashtag: Hashtag
    lateinit var insertedWriter: User
    lateinit var insertedModerator: User
    lateinit var insertedMedia: MediaFile
    lateinit var insertedPost1: Post
    lateinit var insertedPost2: Post
    lateinit var publishedComment1Author1: Comment
    lateinit var hiddenComment1Author1: Comment
    lateinit var hiddenComment2Author1: Comment
    lateinit var publishedComment1Author2: Comment
    lateinit var publishedComment2Author2: Comment
    lateinit var hiddenComment1Author2: Comment
    lateinit var hiddenComment2Author2: Comment

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

        insertedPost1 =
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

        insertedPost2 =
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

        publishedComment1Author1 =
            commentOperations
                .insertComment(
                    validCommentContent,
                    insertedModerator.id,
                    insertedPost1.id,
                    validPostDate1,
                    validPostDate1,
                    false,
                ).shouldNotBeNull()

        hiddenComment1Author1 =
            commentOperations
                .insertComment(
                    validCommentContent,
                    insertedModerator.id,
                    insertedPost1.id,
                    validPostDate2,
                    validPostDate2,
                    true,
                ).shouldNotBeNull()

        hiddenComment2Author1 =
            commentOperations
                .insertComment(
                    validCommentContent,
                    insertedModerator.id,
                    insertedPost1.id,
                    validPostDate1,
                    validPostDate1,
                    true,
                ).shouldNotBeNull()

        publishedComment1Author2 =
            commentOperations
                .insertComment(
                    validCommentContent,
                    insertedWriter.id,
                    insertedPost1.id,
                    validPostDate2,
                    validPostDate2,
                    false,
                ).shouldNotBeNull()

        publishedComment2Author2 =
            commentOperations
                .insertComment(
                    validCommentContent,
                    insertedWriter.id,
                    insertedPost2.id,
                    validPostDate2,
                    validPostDate2,
                    false,
                ).shouldNotBeNull()

        hiddenComment1Author2 =
            commentOperations
                .insertComment(
                    validCommentContent,
                    insertedWriter.id,
                    insertedPost1.id,
                    validPostDate2,
                    validPostDate2,
                    true,
                ).shouldNotBeNull()

        hiddenComment2Author2 =
            commentOperations
                .insertComment(
                    validCommentContent,
                    insertedWriter.id,
                    insertedPost2.id,
                    validPostDate2,
                    validPostDate2,
                    true,
                ).shouldNotBeNull()
    }

    test("published comments can be fetched in first post in descending order") {
        val fetchedComments = commentOperations.selectPublishedCommentsInPost(insertedPost1.id).shouldNotBeNull()

        fetchedComments.size.shouldBe(2)
        fetchedComments[0].creationDate.shouldBe(validPostDate2)
        fetchedComments[1].creationDate.shouldBe(validPostDate1)
    }

    test("published comments can not be fetched in first post in descending order by invalid post id") {
        commentOperations.selectPublishedCommentsInPost(Int.MIN_VALUE).size.shouldBe(0 )
    }

    test("hidden comments can be fetched in first post in descending order") {
        val fetchedComments = commentOperations.selectHiddenCommentsInPost(insertedPost1.id).shouldNotBeNull()

        fetchedComments.size.shouldBe(3)
        fetchedComments[0].creationDate.shouldBe(validPostDate2)
        fetchedComments[1].creationDate.shouldBe(validPostDate2)
        fetchedComments[2].creationDate.shouldBe(validPostDate1)
    }

    test("hidden comments can not be fetched in first post in descending order by invalid post id") {
        commentOperations.selectHiddenCommentsInPost(Int.MIN_VALUE).size.shouldBe(0 )
    }

    test("hidden comments of user can be fetched in first post in descending order") {
        val fetchedComments = commentOperations
            .selectHiddenCommentsOfUserInPost(insertedPost1.id, insertedModerator.id).shouldNotBeNull()

        fetchedComments.size.shouldBe(2)
        fetchedComments[0].creationDate.shouldBe(validPostDate2)
        fetchedComments[1].creationDate.shouldBe(validPostDate1)
    }

    test("hidden comments can not be fetched in first post in descending order by invalid post id or user id") {
        commentOperations.selectHiddenCommentsOfUserInPost(Int.MIN_VALUE, Int.MIN_VALUE).size.shouldBe(0 )
        commentOperations.selectHiddenCommentsOfUserInPost(Int.MIN_VALUE, Int.MIN_VALUE).size.shouldBe(0 )
        commentOperations.selectHiddenCommentsOfUserInPost(Int.MIN_VALUE, Int.MIN_VALUE).size.shouldBe(0 )
    }
})
