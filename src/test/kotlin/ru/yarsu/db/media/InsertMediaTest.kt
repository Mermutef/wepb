package ru.yarsu.db.media

import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import ru.yarsu.db.DatabaseOperationsHolder
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.appConfig
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
import ru.yarsu.domain.operations.OperationsHolder
import java.time.LocalDateTime

class InsertMediaTest : TestcontainerSpec({ context ->
    lateinit var validMedia: MediaFile
    lateinit var teacher: User
    val mediaOperations = MediaOperations(context)

    val database = DatabaseOperationsHolder(context)
    val operations = OperationsHolder(database, appConfig)

    beforeEach {
        teacher = operations
            .userOperations
            .createUser(
                validName,
                validUserSurname,
                validLogin,
                validEmail,
                validPhoneNumber,
                validPass,
                validVKLink,
                Role.MODERATOR,
            ).shouldBeSuccess()

        validMedia = MediaFile(
            filename = "test_media.txt",
            content = "Valid content".toByteArray(),
            mediaType = MediaType.VIDEO,
            birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
            isTemporary = false,
            authorId = teacher.id,
        )
    }

    test("Valid media can be inserted") {

        mediaOperations
            .insertMedia(
                filename = validMedia.filename,
                authorId = validMedia.authorId,
                mediaType = validMedia.mediaType,
                content = validMedia.content,
                isTemporary = validMedia.isTemporary,
                birthDate = validMedia.birthDate,
            ).shouldNotBeNull()
    }

    test("Valid media insertion should return this media") {
        val insertedMedia =
            mediaOperations
                .insertMedia(
                    filename = validMedia.filename,
                    authorId = validMedia.authorId,
                    mediaType = validMedia.mediaType,
                    content = validMedia.content,
                    isTemporary = validMedia.isTemporary,
                    birthDate = validMedia.birthDate,
                ).shouldNotBeNull()

        insertedMedia.filename shouldBe validMedia.filename
        insertedMedia.mediaType shouldBe validMedia.mediaType
        insertedMedia.content.contentEquals(validMedia.content) shouldBe true
        insertedMedia.authorId shouldBe validMedia.authorId
        insertedMedia.isTemporary shouldBe validMedia.isTemporary
        insertedMedia.birthDate shouldBe validMedia.birthDate
    }

    test("Valid media with long filename can be inserted") {
        val insertedMedia =
            mediaOperations
                .insertMedia(
                    filename = "a".repeat(MediaFile.MAX_FILENAME_LENGTH),
                    authorId = validMedia.authorId,
                    mediaType = validMedia.mediaType,
                    content = validMedia.content,
                    isTemporary = validMedia.isTemporary,
                    birthDate = validMedia.birthDate,
                ).shouldNotBeNull()

        insertedMedia.filename shouldBe "a".repeat(MediaFile.MAX_FILENAME_LENGTH)
        insertedMedia.mediaType shouldBe validMedia.mediaType
        insertedMedia.content.contentEquals(validMedia.content) shouldBe true
        insertedMedia.authorId shouldBe validMedia.authorId
        insertedMedia.isTemporary shouldBe validMedia.isTemporary
        insertedMedia.birthDate shouldBe validMedia.birthDate
    }

    test("Temporary media elder than 7 days should be removed after insertion new media") {
        mediaOperations
            .insertMedia(
                filename = validMedia.filename,
                authorId = validMedia.authorId,
                mediaType = validMedia.mediaType,
                content = validMedia.content,
                isTemporary = false,
                birthDate = LocalDateTime.now().minusDays(REMOVING_TEMP_FILES_LIMIT_IN_DAYS),
            ) shouldNotBe null
        mediaOperations.makeTemporary(validMedia.filename) shouldNotBe null
        mediaOperations.selectMediaWithMeta(validMedia.filename) shouldNotBe null
        mediaOperations.insertMedia(
            filename = validMedia.filename + "1",
            authorId = validMedia.authorId,
            mediaType = validMedia.mediaType,
            content = validMedia.content,
            isTemporary = true,
            birthDate = LocalDateTime.now(),
        )
        mediaOperations.selectMediaWithMeta(validMedia.filename) shouldBe null
    }
})
