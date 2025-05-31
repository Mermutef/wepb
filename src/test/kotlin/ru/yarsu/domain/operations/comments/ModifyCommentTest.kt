package ru.yarsu.domain.operations.comments

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Comment
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.hashtags.FieldInHashtagChangingError
import ru.yarsu.domain.operations.validCommentContent
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

class ModifyCommentTest : FunSpec({
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

    val changeContentMock: (commentId: Int, newContent: String) -> Comment? =
        { _, newContent -> validComment.copy(content = newContent) }
    val changeContent = ChangeContentInComment(changeContentMock)
    val changeContentNullMock: (commentId: Int, newContent: String) -> Comment? =
        { _, _ -> null }
    val changeContentNull = ChangeContentInComment(changeContentNullMock)

    val changeVisibilityMock: (commentId: Int) -> Comment? = { _ -> validComment.copy(isHidden = true)}
    val changeVisibility = ChangeVisibilityComment(changeVisibilityMock)
    val changeVisibilityNullMock: (commentId: Int) -> Comment? = { _ -> null}
    val changeVisibilityNull = ChangeVisibilityComment(changeVisibilityNullMock)

    test("Content can be changed to valid content") {
        changeContent(validComment, "${validCommentContent}2").shouldBeSuccess().content shouldBe
                "${validCommentContent}2"
    }

    test("Content cannot be changed to blank content") {
        changeContent(validComment, "  \t\n") shouldBeFailure
                FieldInCommentChangingError.CONTENT_IS_BLANK_OR_EMPTY
    }

    test("Content cannot be changed too long content") {
        changeContent(validComment, "a".repeat(Comment.MAX_CONTENT_LENGTH + 1)) shouldBeFailure
                FieldInCommentChangingError.CONTENT_IS_TOO_LONG
    }

    test("Unknown db error test for changeContent") {
        changeContentNull(validComment, "${validCommentContent}2") shouldBeFailure
                FieldInCommentChangingError.UNKNOWN_CHANGING_ERROR
    }

    test("Visibility can be changed") {
        changeVisibility(validComment).shouldBeSuccess().isHidden shouldBe true
    }

    test("Unknown db error test for changeVisibility") {
        changeVisibilityNull(validComment) shouldBeFailure
                FieldInCommentChangingError.UNKNOWN_CHANGING_ERROR
    }
})