package ru.yarsu.db.directions

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
import ru.yarsu.db.validPhoneNumber
import ru.yarsu.db.validSecondPhoneNumber
import ru.yarsu.db.validUserSurname
import ru.yarsu.db.validVKLink
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class InsertDirectionTest : TestcontainerSpec({ context ->

    val directionOperations = DirectionOperations(context)
    val userOperations = UserOperations(context)
    val mediaOperations = MediaOperations(context)

    lateinit var chairman: User
    lateinit var deputyChairman: User
    lateinit var logo: MediaFile
    lateinit var banner: MediaFile

    beforeEach {
        chairman =
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

        deputyChairman =
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

        logo =
            mediaOperations
                .insertMedia(
                    filename = validFileName,
                    authorId = chairman.id,
                    mediaType = MediaType.VIDEO,
                    content = "Valid content".toByteArray(),
                    birthDate = LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                ).shouldNotBeNull()

        banner =
            mediaOperations
                .insertMedia(
                    filename = validFileName,
                    authorId = deputyChairman.id,
                    mediaType = MediaType.VIDEO,
                    birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
                    content = "Valid content".toByteArray(),
                ).shouldNotBeNull()
    }

    test("Valid direction insertion should return this direction") {
        val insertedDirection =
            directionOperations
                .insertDirection(
                    validDirectionName,
                    validDirectionDescription,
                    logo.filename,
                    banner.filename,
                    chairman.id,
                    deputyChairman.id,
                ).shouldNotBeNull()

        insertedDirection.name.shouldBe(validDirectionName)
        insertedDirection.description.shouldBe(validDirectionDescription)
        insertedDirection.logoPath.shouldBe(logo.filename)
        insertedDirection.bannerPath.shouldBe(banner.filename)
        insertedDirection.chairmanId.shouldBe(chairman.id)
        insertedDirection.deputyChairmanId.shouldBe(deputyChairman.id)
    }

    test("Valid direction with long name can be inserted") {
        val insertedDirection =
            directionOperations
                .insertDirection(
                    "a".repeat(Direction.MAX_NAME_LENGTH),
                    validDirectionDescription,
                    logo.filename,
                    banner.filename,
                    chairman.id,
                    deputyChairman.id,
                ).shouldNotBeNull()

        insertedDirection.name.shouldBe("a".repeat(Direction.MAX_NAME_LENGTH))
        insertedDirection.description.shouldBe(validDirectionDescription)
        insertedDirection.logoPath.shouldBe(logo.filename)
        insertedDirection.bannerPath.shouldBe(banner.filename)
        insertedDirection.chairmanId.shouldBe(chairman.id)
        insertedDirection.deputyChairmanId.shouldBe(deputyChairman.id)
    }
})
