package ru.yarsu.domain.operations.comments

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.db.validCommentContent
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Comment
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
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
import java.time.LocalDateTime
import java.time.ZonedDateTime

class FetchCommentTest : FunSpec({
    val validHashtag = Hashtag(1, validHashtagTitle)
    val hashtags = listOf(validHashtag)
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
    val validMedia = MediaFile(
        filename = validPostPreview,
        content = "Valid content".toByteArray(),
        mediaType = MediaType.VIDEO,
        birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
        isTemporary = false,
        authorId = validWriter.id,
    )
    val media = listOf(validMedia)
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
    val validComment = Comment(
        1,
        validCommentContent,
        validWriter.id,
        validPost.id,
        validPostDate1,
        validPostDate1,
        false
    )
    val comments = listOf(validComment)
    val fetchPublishedCommentsInPostMock: (Int) -> List<Comment> = { postId ->
        comments.filter { it.postId == postId && !it.isHidden }
    }
    val fetchHiddenCommentsInPostMock: (Int) -> List<Comment> = { postId ->
        comments.filter { it.postId == postId && it.isHidden }
    }
    val fetchHiddenCommentsOfUserInPostMock: (Int, Int) -> List<Comment> = {postId, authorId ->
        comments.filter { it.postId == postId && it.authorId == authorId && it.isHidden }
    }

    val fetchPublishedCommentsInPost: FetchPublishedCommentsInPost =
        FetchPublishedCommentsInPost(fetchPublishedCommentsInPostMock)
    val fetchHiddenCommentsInPost: FetchHiddenCommentsInPost =
        FetchHiddenCommentsInPost(fetchHiddenCommentsInPostMock)
    val fetchHiddenCommentsOfUserInPost: FetchHiddenCommentsOfUserInPost =
        FetchHiddenCommentsOfUserInPost(fetchHiddenCommentsOfUserInPostMock)

    test("Published comments can be fetched by post id") {
        fetchPublishedCommentsInPost(validPost.id).shouldBeSuccess()
    }
    test("Hidden comments can be fetched by post id") {
        fetchHiddenCommentsInPost(validPost.id).shouldBeSuccess()
    }
    test("Hidden comments can be fetched by post id and author id") {
        fetchHiddenCommentsOfUserInPost(validPost.id, validWriter.id).shouldBeSuccess()
    }
})