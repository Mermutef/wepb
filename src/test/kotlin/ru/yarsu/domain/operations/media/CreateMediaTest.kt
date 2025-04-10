package ru.yarsu.domain.operations.media

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.db.validEmail
import ru.yarsu.db.validLogin
import ru.yarsu.db.validName
import ru.yarsu.db.validPass
import ru.yarsu.db.validPhoneNumber
import ru.yarsu.db.validUserSurname
import ru.yarsu.db.validVKLink
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class CreateMediaTest : FunSpec({
    val validStudent = User(
        1,
        validName,
        validUserSurname,
        validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.READER
    )
    val validMedia = MediaFile(
        filename = "test_media.txt",
        content = "Valid content".toByteArray(),
        mediaType = MediaType.VIDEO,
        birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
        isTemporary = false,
        authorId = validStudent.id,
    )

    val medias = mutableListOf<MediaFile>()

    val createMediaMock: (
        filename: String,
        authorId: Int,
        mediaType: MediaType,
        birthDate: LocalDateTime,
        content: ByteArray,
        isTemporary: Boolean,
    ) -> MediaFile? = { filename, authorId, mediaType, birthDate, content, isTemporary ->
        MediaFile(
            filename,
            authorId,
            birthDate,
            mediaType,
            content,
            isTemporary,
        )
    }

    val createMediaNullMock: (
        filename: String,
        authorId: Int,
        mediaType: MediaType,
        birthDate: LocalDateTime,
        content: ByteArray,
        isTemporary: Boolean,
    ) -> MediaFile? = { _, _, _, _, _, _ -> null }

    val fetchOnlyMediaMock: (filename: String) -> ByteArray? =
        { filename -> medias.firstOrNull { it.filename == filename }?.content }

    val createMedia = CreateMedia(fetchOnlyMediaMock, createMediaMock)

    val createMediaNull = CreateMedia(fetchOnlyMediaMock, createMediaNullMock)

    beforeEach {
        medias.removeAll(medias)
    }

    test("Media with long filename cannot be created") {
        createMedia(
            "TenSymbols".repeat(39),
            validStudent,
            validMedia.mediaType,
            validMedia.birthDate,
            validMedia.content,
            validMedia.isTemporary,
        ) shouldBeFailure MediaCreationError.FILENAME_IS_TOO_LONG
    }

    test("Media with empty filename cannot be created") {
        createMedia(
            "",
            validStudent,
            validMedia.mediaType,
            validMedia.birthDate,
            validMedia.content,
            validMedia.isTemporary,
        ) shouldBeFailure MediaCreationError.FILENAME_IS_BLANK_OR_EMPTY
    }

    test("Empty media cannot be created") {
        createMedia(
            "some-media",
            validStudent,
            validMedia.mediaType,
            validMedia.birthDate,
            byteArrayOf(),
            validMedia.isTemporary,
        ) shouldBeFailure MediaCreationError.MEDIA_IS_EMPTY
    }

    listOf(
        "some media",
        "какое-то медиа",
        "Dadf fdlsakKLJKL",
        "dkfjk#1@kdfsk",
    ).forEach { invalidFilename ->
        test("Media with invalid filename $invalidFilename cannot be created") {
            createMedia(
                invalidFilename,
                validStudent,
                validMedia.mediaType,
                validMedia.birthDate,
                validMedia.content,
                validMedia.isTemporary,
            ) shouldBeFailure MediaCreationError.FILENAME_PATTERN_MISMATCH
        }
    }

    test("Media with filename that already exist can not be created") {
        val newMedia = createMedia(
            validMedia.filename,
            validStudent,
            validMedia.mediaType,
            validMedia.birthDate,
            validMedia.content,
            validMedia.isTemporary,
        ).shouldBeSuccess()

        medias.add(newMedia)

        createMedia(
            validMedia.filename,
            validStudent,
            validMedia.mediaType,
            validMedia.birthDate,
            "Another content".toByteArray(),
            validMedia.isTemporary,
        ) shouldBeFailure MediaCreationError.FILENAME_ALREADY_EXIST
    }

    test("Media with identical content and filename can not be created") {
        val newMedia = createMedia(
            validMedia.filename,
            validStudent,
            validMedia.mediaType,
            validMedia.birthDate,
            validMedia.content,
            validMedia.isTemporary,
        ).shouldBeSuccess()

        medias.add(newMedia)

        createMedia(
            validMedia.filename,
            validStudent.copy(id = validStudent.id + 2),
            validMedia.mediaType,
            validMedia.birthDate.minusDays(1L),
            validMedia.content,
            !validMedia.isTemporary,
        ) shouldBeFailure MediaCreationError.MEDIA_ALREADY_EXIST
    }

    test("Unknown db error for CreateMedia test") {
        createMediaNull(
            validMedia.filename,
            validStudent,
            validMedia.mediaType,
            validMedia.birthDate,
            validMedia.content,
            validMedia.isTemporary,
        ) shouldBeFailure MediaCreationError.UNKNOWN_DATABASE_ERROR
    }
})
