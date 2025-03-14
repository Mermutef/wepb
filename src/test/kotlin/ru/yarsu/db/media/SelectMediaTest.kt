package ru.yarsu.db.media

import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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

class SelectMediaTest : TestcontainerSpec({ context ->
    val mediaOperations = MediaOperations(context)

    val database = DatabaseOperationsHolder(context)
    val operations = OperationsHolder(database, appConfig)

    lateinit var validMedia: MediaFile
    lateinit var teacher: User

    beforeEach {
        teacher =
            operations
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
            isTemporary = false,
            authorId = teacher.id,
        )
    }

    test("Fetch media with meta by filename should return that media and its meta") {
        mediaOperations
            .insertMedia(
                filename = validMedia.filename,
                authorId = validMedia.authorId,
                mediaType = validMedia.mediaType,
                content = validMedia.content,
                isTemporary = validMedia.isTemporary,
                birthDate = validMedia.birthDate,
            ).shouldNotBeNull()

        val fetchedMedia = mediaOperations.selectMediaWithMeta(validMedia.filename).shouldNotBeNull()

        fetchedMedia.filename shouldBe validMedia.filename
        fetchedMedia.mediaType shouldBe validMedia.mediaType
        fetchedMedia.content.contentEquals(validMedia.content) shouldBe true
        fetchedMedia.authorId shouldBe validMedia.authorId
        fetchedMedia.isTemporary shouldBe validMedia.isTemporary
        fetchedMedia.birthDate shouldBe validMedia.birthDate
    }

    test("Fetch only meta by filename should return only that media meta") {
        mediaOperations
            .insertMedia(
                filename = validMedia.filename,
                authorId = validMedia.authorId,
                mediaType = validMedia.mediaType,
                content = validMedia.content,
                isTemporary = validMedia.isTemporary,
                birthDate = validMedia.birthDate,
            ).shouldNotBeNull()

        val fetchedMedia = mediaOperations.selectOnlyMeta(validMedia.filename).shouldNotBeNull()

        fetchedMedia.filename shouldBe validMedia.filename
        fetchedMedia.mediaType shouldBe validMedia.mediaType
        fetchedMedia.content.isEmpty() shouldBe true
        fetchedMedia.authorId shouldBe validMedia.authorId
        fetchedMedia.isTemporary shouldBe validMedia.isTemporary
        fetchedMedia.birthDate shouldBe validMedia.birthDate
    }

    test("Fetch only media by filename should return only that media without meta") {
        mediaOperations
            .insertMedia(
                filename = validMedia.filename,
                authorId = validMedia.authorId,
                mediaType = validMedia.mediaType,
                content = validMedia.content,
                isTemporary = validMedia.isTemporary,
                birthDate = validMedia.birthDate,
            ).shouldNotBeNull()

        val fetchedMedia = mediaOperations.selectOnlyMedia(validMedia.filename).shouldNotBeNull()

        fetchedMedia.contentEquals(validMedia.content) shouldBe true
    }

    test("Fetch media by filename should return null if name is not valid") {
        mediaOperations.selectMediaWithMeta("Dn aslk") shouldBe null
        mediaOperations.selectOnlyMedia("Dn aslk") shouldBe null
        mediaOperations.selectOnlyMeta("Dn aslk") shouldBe null
    }
})
