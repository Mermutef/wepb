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
import ru.yarsu.db.validPhoneNumbers
import ru.yarsu.db.validUserSurname
import ru.yarsu.db.validVKLink
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class UpdateDirectionTest : TestcontainerSpec({ context ->

    val directionOperations = DirectionOperations(context)
    val userOperations = UserOperations(context)
    val mediaOperations = MediaOperations(context)

    lateinit var chairman: User
    lateinit var deputyChairman: User
    lateinit var mediaFile: MediaFile
    lateinit var direction: Direction

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

        direction =
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

    test("Direction can be updated") {
        val newChairman =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    "new" + validLogin,
                    "new" + validEmail,
                    validPhoneNumbers[2],
                    validPass,
                    validVKLink,
                    Role.WRITER,
                )
                .shouldNotBeNull()

        val newDeputyChairman =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    "new" + "${validLogin}2",
                    "new" + "${validEmail}2",
                    validPhoneNumbers[3],
                    validPass,
                    validVKLink,
                    Role.WRITER,
                )
                .shouldNotBeNull()

        val updateDirection =
            directionOperations
                .updateDirection(
                    direction.id,
                    "КВН",
                    "Клуб внимательных и находчивых",
                    mediaFile.filename,
                    mediaFile.filename,
                    newChairman.id,
                    newDeputyChairman.id,
                )
                .shouldNotBeNull()

        updateDirection.name.shouldBe("КВН")
        updateDirection.description.shouldBe("Клуб внимательных и находчивых")
        updateDirection.logoPath.shouldBe(mediaFile.filename)
        updateDirection.bannerPath.shouldBe(mediaFile.filename)
        updateDirection.chairmanId.shouldBe(newChairman.id)
        updateDirection.deputyChairmanId.shouldBe(newDeputyChairman.id)
    }

    test("Direction chairman can be changed") {
        val newChairmanId = 2
        directionOperations
            .updateChairman(direction.id, newChairmanId)
            .shouldNotBeNull().chairmanId shouldBe newChairmanId
    }

    test("Direction deputy chairman can be changed") {
        val newDeputyChairmanId = 2
        directionOperations
            .updateDeputyChairman(direction.id, newDeputyChairmanId)
            .shouldNotBeNull().deputyChairmanId shouldBe newDeputyChairmanId
    }

    test("Direction can be deleted") {
        directionOperations
            .deleteDirection(direction.id)
            .shouldNotBeNull()
        directionOperations.selectDirectionByID(direction.id) shouldBe null
    }
})
