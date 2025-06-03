package ru.yarsu.db.directions

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.media.MediaOperations
import ru.yarsu.db.users.UserOperations
import ru.yarsu.db.validDirectionDescription
import ru.yarsu.db.validDirectionName
import ru.yarsu.db.validEmail
import ru.yarsu.db.validFileName
import ru.yarsu.db.validLogin
import ru.yarsu.db.validName
import ru.yarsu.db.validPass
import ru.yarsu.db.validPhoneNumbers
import ru.yarsu.db.validUserSurname
import ru.yarsu.db.validVKLink
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class SelectDirectionTest : TestcontainerSpec({ context ->

    val directionOperations = DirectionOperations(context)
    val userOperations = UserOperations(context)
    val mediaOperations = MediaOperations(context)

    lateinit var chairman: User
    lateinit var deputyChairman: User
    lateinit var mediaFile: MediaFile
    lateinit var insertedDirection: Direction

    beforeEach {
        chairman =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumbers[0],
                    validPass,
                    validVKLink,
                    Role.WRITER,
                )
                .shouldNotBeNull()

        deputyChairman =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    "${validLogin}2",
                    "${validEmail}2",
                    validPhoneNumbers[1],
                    validPass,
                    validVKLink,
                    Role.WRITER,
                )
                .shouldNotBeNull()

        mediaFile =
            mediaOperations
                .insertMedia(
                    filename = validFileName,
                    authorId = chairman.id,
                    mediaType = MediaType.VIDEO,
                    birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
                    content = "Valid content".toByteArray(),
                ).shouldNotBeNull()

        insertedDirection =
            directionOperations
                .insertDirection(
                    validDirectionName,
                    validDirectionDescription,
                    mediaFile.filename,
                    mediaFile.filename,
                    chairman.id,
                    deputyChairman.id,
                ).shouldNotBeNull()
    }

    test("There is only direction by default") {
        directionOperations
            .selectAllDirections()
            .shouldNotBeNull()
            .size
            .shouldBe(1)
    }

    test("There is only direction by default") {
        directionOperations
            .selectAllDirectionsNameAndLogo()
            .shouldNotBeNull()
            .size
            .shouldBe(1)
    }

    test("If two directions were added, there should be exactly two directions") {

        // already added one

        directionOperations
            .insertDirection(
                "КВН",
                "Клуб внимательных и находчивых",
                mediaFile.filename,
                mediaFile.filename,
                chairman.id,
                deputyChairman.id,
            )

        directionOperations
            .selectAllDirections()
            .shouldNotBeNull()
            .shouldHaveSize(2)
    }

    test("Direction can be fetched by valid ID") {
        val fetchedDirection =
            directionOperations
                .selectDirectionByID(insertedDirection.id)
                .shouldNotBeNull()

        fetchedDirection.id.shouldBe(insertedDirection.id)
        fetchedDirection.name.shouldBe(insertedDirection.name)
        fetchedDirection.description.shouldBe(insertedDirection.description)
        fetchedDirection.logoPath.shouldBe(insertedDirection.logoPath)
        fetchedDirection.bannerPath.shouldBe(insertedDirection.bannerPath)
        fetchedDirection.chairmanId.shouldBe(insertedDirection.chairmanId)
        fetchedDirection.deputyChairmanId.shouldBe(insertedDirection.deputyChairmanId)
    }

    test("Direction can't be fetched by invalid ID") {
        directionOperations
            .selectDirectionByID(Int.MIN_VALUE)
            .shouldBeNull()
    }

    test("Direction's name and logo can be fetched by valid ID") {
        val fetchedDirection =
            directionOperations
                .selectDirectionNameAndLogoByID(insertedDirection.id)
                .shouldNotBeNull()

        fetchedDirection.id.shouldBe(insertedDirection.id)
        fetchedDirection.name.shouldBe(insertedDirection.name)
        fetchedDirection.description.shouldBe("")
        fetchedDirection.logoPath.shouldBe(insertedDirection.logoPath)
        fetchedDirection.bannerPath.shouldBe("")
        fetchedDirection.chairmanId.shouldBe(-1)
        fetchedDirection.deputyChairmanId.shouldBe(-1)
    }

    test("Direction's name and logo can't be fetched by invalid ID") {
        directionOperations
            .selectDirectionNameAndLogoByID(Int.MIN_VALUE)
            .shouldBeNull()
    }

    test("Chairman id can be fetched by valid direction ID ") {
        directionOperations
            .selectChairmanIDByDirectionID(insertedDirection.id)
            .shouldNotBeNull() shouldBe (insertedDirection.chairmanId)
    }

    test("Chairman id can't be fetched by invalid direction ID") {
        directionOperations
            .selectChairmanIDByDirectionID(Int.MIN_VALUE)
            .shouldBeNull()
    }

    test("Direction's name and logo can be fetched by valid ID") {
        directionOperations
            .selectDeputyChairmanIDByDirectionID(insertedDirection.id)
            .shouldNotBeNull() shouldBe (insertedDirection.deputyChairmanId)
    }

    test("Direction's name and logo can't be fetched by invalid ID") {
        directionOperations
            .selectDeputyChairmanIDByDirectionID(Int.MIN_VALUE)
            .shouldBeNull()
    }
})
