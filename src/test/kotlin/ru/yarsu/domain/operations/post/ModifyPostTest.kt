package ru.yarsu.domain.operations.post

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.posts.ChangeDateFieldInPost
import ru.yarsu.domain.operations.posts.ChangeHashtagIdInPost
import ru.yarsu.domain.operations.posts.ChangePreviewInPost
import ru.yarsu.domain.operations.posts.ChangeStringFieldInPost
import ru.yarsu.domain.operations.posts.ChangeUserIdInPost
import ru.yarsu.domain.operations.posts.FieldInPostChangingError
import ru.yarsu.domain.operations.posts.MakeStatusError
import ru.yarsu.domain.operations.posts.StatusChanger
import ru.yarsu.domain.operations.validEmail
import ru.yarsu.domain.operations.validHashtagTitle
import ru.yarsu.domain.operations.validName
import ru.yarsu.domain.operations.validPass
import ru.yarsu.domain.operations.validPhoneNumber
import ru.yarsu.domain.operations.validPostContent
import ru.yarsu.domain.operations.validPostDate1
import ru.yarsu.domain.operations.validPostPreview
import ru.yarsu.domain.operations.validPostTitle
import ru.yarsu.domain.operations.validVKLink
import java.time.LocalDateTime
import java.time.ZonedDateTime

class ModifyPostTest : FunSpec({
    val validHashtag = Hashtag(1, validHashtagTitle)
    val validSecondHashtag = Hashtag(2, "${validHashtagTitle}2")
    val hashtags = listOf(validHashtag, validSecondHashtag)
    val validWriter = User(
        1,
        validName,
        ru.yarsu.domain.operations.validUserSurname,
        ru.yarsu.domain.operations.validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.WRITER
    )
    val validModerator = User(
        2,
        validName,
        ru.yarsu.domain.operations.validUserSurname,
        "${ru.yarsu.domain.operations.validLogin}2",
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
    val validSecondMedia = MediaFile(
        filename = "${validPostPreview}2",
        content = "Valid content".toByteArray(),
        mediaType = MediaType.VIDEO,
        birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
        isTemporary = false,
        authorId = validWriter.id,
    )
    val invalidMedia = MediaFile(
        filename = "a".repeat(Post.MAX_PREVIEW_LENGTH + 100),
        content = "Valid content".toByteArray(),
        mediaType = MediaType.VIDEO,
        birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
        isTemporary = false,
        authorId = validWriter.id,
    )
    val media = listOf(validMedia, validSecondMedia, invalidMedia)
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
        Status.MODERATION
    )

    val validSecondPost = Post(
        2,
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

    val changeStringFieldMock: (Int, String, ZonedDateTime) -> Post? = { _, newStringField, newDate ->
        validPost.copy(title = newStringField, lastModifiedDate = newDate)
    }
    val changeDateFieldMock: (Int, ZonedDateTime, ZonedDateTime) -> Post? = { _, newDateField, newDate ->
        validPost.copy(eventDate = newDateField, lastModifiedDate = newDate)
    }
    val changeHashtagIdMock: (Int, Int, ZonedDateTime) -> Post? = { _, newHashtagID, newDate ->
        validPost.copy(hashtagId = newHashtagID, lastModifiedDate = newDate)
    }
    val fetchHashtagByIdMock: (Int) -> Hashtag? = { hashtagId ->
        hashtags.firstOrNull { it.id == hashtagId }
    }
    val changeUserIdMock: (Int, Int, ZonedDateTime) -> Post? = { _, newUserId, newDate ->
        validPost.copy(authorId = newUserId, lastModifiedDate = newDate)
    }
    val fetchUserByIdMock: (Int) -> User? = { userId ->
        users.firstOrNull { it.id == userId }
    }
    val changePreviewMock: (Int, String, ZonedDateTime) -> Post? = { _, newPreview, newDate ->
        validPost.copy(preview = newPreview, lastModifiedDate = newDate)
    }
    val fetchMediaByNameMock: (String) -> MediaFile? = { name ->
        media.firstOrNull { it.filename == name }
    }
    val changeStatusMock: (Post, Status, ZonedDateTime) -> Post? = { _, newStatus, newDate ->
        validPost.copy(status = newStatus, lastModifiedDate = newDate)
    }

    val maxLength = 100
    val zeroLength = 0
    val validPattern = Regex("^[а-яА-Я -]+\$")
    val zeroPattern = Regex(".*")

    val changeStringField = ChangeStringFieldInPost(
        maxLength = maxLength,
        pattern = validPattern,
        changeField = changeStringFieldMock
    )
    val changeStringFieldWithZeroLength = ChangeStringFieldInPost(
        maxLength = zeroLength,
        pattern = validPattern,
        changeField = changeStringFieldMock
    )
    val changeStringFieldWithZeroPattern = ChangeStringFieldInPost(
        maxLength = maxLength,
        pattern = zeroPattern,
        changeField = changeStringFieldMock
    )
    val changeDateField = ChangeDateFieldInPost(
        changeDateFieldMock
    )
    val changeHashtagId = ChangeHashtagIdInPost(
        changeHashtagIdMock,
        fetchHashtagByIdMock
    )
    val changeUserId = ChangeUserIdInPost(
        changeUserIdMock,
        fetchUserByIdMock
    )
    val changePreview = ChangePreviewInPost(
        changePreviewMock,
        fetchMediaByNameMock
    )
    val changeStatus = StatusChanger(
        Status.DRAFT,
        alreadyHasStatusError = MakeStatusError.IS_ALREADY_DRAFT,
        updateStatus = changeStatusMock,
        unknownError = MakeStatusError.UNKNOWN_DATABASE_ERROR
    )

    val changeStringFieldNullMock: (Int, String, ZonedDateTime) -> Post? = { _, _, _ -> null }
    val changeDateFieldNullMock: (Int, ZonedDateTime, ZonedDateTime) -> Post? = { _, _, _ -> null }
    val changeHashtagIdNullMock: (Int, Int, ZonedDateTime) -> Post? = { _, _, _ -> null }
    val changeUserIdNullMock: (Int, Int, ZonedDateTime) -> Post? = { _, _, _ -> null }
    val changePreviewNullMock: (Int, String, ZonedDateTime) -> Post? = { _, _, _ -> null }
    val changeStatusNullMock: (Post, Status, ZonedDateTime) -> Post? = { _, _, _ -> null }

    val changeStringFieldNull = ChangeStringFieldInPost(
        maxLength = maxLength,
        pattern = validPattern,
        changeField = changeStringFieldNullMock
    )
    val changeDateFieldNull = ChangeDateFieldInPost(
        changeDateFieldNullMock
    )
    val changeHashtagIdNull = ChangeHashtagIdInPost(
        changeHashtagIdNullMock,
        fetchHashtagByIdMock
    )
    val changeUserIdNull = ChangeUserIdInPost(
        changeUserIdNullMock,
        fetchUserByIdMock
    )
    val changePreviewNull = ChangePreviewInPost(
        changePreviewNullMock,
        fetchMediaByNameMock
    )
    val changeStatusNull = StatusChanger(
        Status.DRAFT,
        alreadyHasStatusError = MakeStatusError.IS_ALREADY_DRAFT,
        updateStatus = changeStatusNullMock,
        unknownError = MakeStatusError.UNKNOWN_DATABASE_ERROR
    )

    test("String field can be changed to valid string field") {
        changeStringField(validPost, "Вася").shouldBeSuccess().title shouldBe "Вася"
    }
    test("String field can not be change to invalid string field") {
        changeStringField(validPost, "Vasy").shouldBeFailure(FieldInPostChangingError.FIELD_PATTERN_MISMATCH)
    }
    test("String field can not be changed to long string field") {
        changeStringField(validPost, "а".repeat(maxLength + 1))
            .shouldBeFailure(FieldInPostChangingError.FIELD_IS_TOO_LONG)
    }
    test("String field can not be change to blank string field") {
        changeStringField(validPost, "").shouldBeFailure(FieldInPostChangingError.FIELD_IS_BLANK_OR_EMPTY)
    }
    test("String field can be change without max length") {
        changeStringFieldWithZeroLength(validPost, "Вася").shouldBeSuccess().title shouldBe "Вася"
    }
    test("String field can be change without pattern") {
        changeStringFieldWithZeroPattern(validPost, "Vasy").shouldBeSuccess().title shouldBe "Vasy"
    }
    test("Date field can be changed to date field") {
        changeDateField(validPost, validPostDate1).shouldBeSuccess().eventDate shouldBe validPostDate1
    }
    test("Hashtag id can be changed to valid hashtag id") {
        changeHashtagId(validPost, validSecondHashtag.id).shouldBeSuccess().hashtagId shouldBe validSecondHashtag.id
    }
    test("Hashtag id can be changed to invalid hashtag id") {
        changeHashtagId(validPost, Int.MIN_VALUE).shouldBeFailure(FieldInPostChangingError.HASHTAG_NOT_EXISTS)
    }
    test("User id can be changed to valid user id") {
        changeUserId(validPost, validModerator.id).shouldBeSuccess().authorId shouldBe validModerator.id
    }
    test("User id can not be changed to invalid user id") {
        changeUserId(validPost, Int.MIN_VALUE).shouldBeFailure(FieldInPostChangingError.USER_NOT_EXISTS)
    }
    test("Preview can be changed to valid preview") {
        changePreview(validPost, validSecondMedia.filename).shouldBeSuccess()
            .preview shouldBe validSecondMedia.filename
    }
    test("Preview can not be changed to long preview") {
        changePreview(validPost, "a".repeat(Post.MAX_PREVIEW_LENGTH + 100))
            .shouldBeFailure(FieldInPostChangingError.FIELD_IS_TOO_LONG)
    }
    test("Preview can not be change to invalid preview") {
        changePreview(validPost, "Vasy").shouldBeFailure(FieldInPostChangingError.MEDIA_NOT_EXISTS)
    }
    test("Status can be changed") {
        changeStatus(validPost).shouldBeSuccess().status shouldBe Status.DRAFT
    }
    test("Status can not be changed same status") {
        changeStatus(validSecondPost).shouldBeFailure(MakeStatusError.IS_ALREADY_DRAFT)
    }
    test("Unknown db error test for changeStringField") {
        changeStringFieldNull(validPost, "Вася")
            .shouldBeFailure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
    }
    test("Unknown db error test for changeDateField") {
        changeDateFieldNull(validPost, validPostDate1)
            .shouldBeFailure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
    }
    test("Unknown db error test for changeHashtagId") {
        changeHashtagIdNull(validPost, validSecondHashtag.id)
            .shouldBeFailure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
    }
    test("Unknown db error test for changeUserId") {
        changeUserIdNull(validPost, validModerator.id)
            .shouldBeFailure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
    }
    test("Unknown db error test for changePreview") {
        changePreviewNull(validPost, validSecondMedia.filename)
            .shouldBeFailure(FieldInPostChangingError.UNKNOWN_CHANGING_ERROR)
    }
    test("Unknown db error test for changeStatus") {
        changeStatusNull(validPost)
            .shouldBeFailure(MakeStatusError.UNKNOWN_DATABASE_ERROR)
    }
})
