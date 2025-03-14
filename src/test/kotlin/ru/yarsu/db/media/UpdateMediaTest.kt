package ru.yarsu.db.media

import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import ru.yarsu.db.DatabaseOperationsHolder
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.appConfig
import ru.yarsu.db.validEmail
import ru.yarsu.db.validPass
import ru.yarsu.db.validUserName
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.OperationsHolder
import java.time.LocalDateTime

class UpdateMediaTest : TestcontainerSpec({ context ->
    lateinit var validMedia: MediaFile
    lateinit var teacher: User
    val mediaOperations = MediaOperations(context)

    val database = DatabaseOperationsHolder(context)
    val operations = OperationsHolder(database, appConfig)

    beforeEach {
        teacher = operations
            .userOperations
            .createUser(
                validUserName,
                validEmail,
                validPass,
                Role.MODERATOR,
            ).shouldBeSuccess()

        validMedia = MediaFile(
            filename = "test_media.txt",
            content = "Valid content".toByteArray(),
            mediaType = MediaType.VIDEO,
            birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
            authorId = teacher.id,
        )
    }

    test("Media should be temporary by default") {
        mediaOperations
            .insertMedia(
                filename = validMedia.filename,
                authorId = validMedia.authorId,
                mediaType = validMedia.mediaType,
                content = validMedia.content,
                birthDate = validMedia.birthDate,
            ).shouldNotBeNull()
        validMedia.isTemporary.shouldBeTrue()
    }

    test("Media temporary mark can be changed by valid request") {
        mediaOperations
            .insertMedia(
                filename = validMedia.filename,
                authorId = validMedia.authorId,
                mediaType = validMedia.mediaType,
                content = validMedia.content,
                birthDate = validMedia.birthDate,
            ).shouldNotBeNull()
        mediaOperations.makeRegular(validMedia.filename) shouldNotBe null
        mediaOperations.selectMediaWithMeta(validMedia.filename)?.isTemporary shouldBe false

        mediaOperations.makeTemporary(validMedia.filename) shouldNotBe null
        mediaOperations.selectMediaWithMeta(validMedia.filename)?.isTemporary shouldBe true
    }

    test("Media can be deleted from database") {
        mediaOperations
            .insertMedia(
                filename = validMedia.filename,
                authorId = validMedia.authorId,
                mediaType = validMedia.mediaType,
                content = validMedia.content,
                birthDate = validMedia.birthDate,
            ).shouldNotBeNull()
        mediaOperations.deleteMedia(validMedia.filename)
        mediaOperations.selectMediaWithMeta(validMedia.filename) shouldBe null
    }
})
