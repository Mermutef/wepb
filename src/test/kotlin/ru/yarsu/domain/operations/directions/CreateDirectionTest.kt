package ru.yarsu.domain.operations.directions

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.db.validDirectionDescription
import ru.yarsu.db.validDirectionName
import ru.yarsu.db.validFileName
import ru.yarsu.db.validLogin
import ru.yarsu.db.validSecondPhoneNumber
import ru.yarsu.db.validUserSurname
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class CreateDirectionTest : FunSpec({
    val directions = mutableListOf<Direction>()

    val chairman = User(
        0,
        ru.yarsu.db.validName,
        validUserSurname,
        validLogin,
        ru.yarsu.db.validEmail,
        ru.yarsu.db.validPhoneNumber,
        ru.yarsu.db.validPass,
        ru.yarsu.db.validVKLink,
        Role.WRITER,
    )
    val deputyChairman = User(
        1,
        ru.yarsu.db.validName,
        validUserSurname,
        "${validLogin}2",
        "${ru.yarsu.db.validEmail}2",
        validSecondPhoneNumber,
        ru.yarsu.db.validPass,
        ru.yarsu.db.validVKLink,
        Role.WRITER,
    )
    val logo = MediaFile(
        filename = validFileName,
        authorId = chairman.id,
        mediaType = MediaType.VIDEO,
        content = "Valid content".toByteArray(),
        birthDate = LocalDateTime.of(2000, 1, 1, 1, 1, 1),
    )
    val banner = MediaFile(
        filename = validFileName,
        authorId = deputyChairman.id,
        mediaType = MediaType.VIDEO,
        birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
        content = "Valid content".toByteArray(),
    )

    beforeEach {
        directions.clear()
    }

    val insertDirectionMock: (
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int,
    ) ->
    Direction? = { name, description, logoPath, bannerPath, chairmanId, deputyChairmanId ->
        val direction =
            Direction(
                id = directions.size + 1,
                name,
                description,
                logoPath,
                bannerPath,
                chairmanId,
                deputyChairmanId
            )
        directions.add(direction)
        direction
    }
    val fetchDirectionByNameMock: (String) -> Direction? = { directionName ->
        directions.firstOrNull { it.name == directionName }
    }

    val insertDirectionNullMock: (
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int,
    ) ->
    Direction? = { _, _, _, _, _, _ -> null }
    val fetchDirectionByNameNullMock: (String) -> Direction? = { _ -> null }

    val createDirection = CreateDirection(
        insertDirectionMock,
        fetchDirectionByNameMock
    )

    val createDirectionNullName = CreateDirection(
        insertDirectionNullMock,
        fetchDirectionByNameNullMock
    )

    test("Valid direction can be inserted") {
        createDirection(
            validDirectionName,
            validDirectionDescription,
            logo.filename,
            banner.filename,
            chairman.id,
            deputyChairman.id,
        )
            .shouldBeSuccess()
    }

    listOf(
        "",
        "Sport",
        "спорт".repeat(Direction.MAX_NAME_LENGTH + 1),
    ).forEach { invalidName ->
        test("Direction with invalid name should not be inserted ($invalidName)") {
            createDirection(
                invalidName,
                validDirectionDescription,
                logo.filename,
                banner.filename,
                chairman.id,
                deputyChairman.id,
            ).shouldBeFailure(DirectionCreationError.INVALID_DIRECTION_DATA)
        }
    }

    listOf(
        "",
        "Opisanie",
    ).forEach { invalidDescription ->
        test("Direction with invalid description should not be inserted ($invalidDescription)") {
            createDirection(
                validDirectionName,
                invalidDescription,
                logo.filename,
                banner.filename,
                chairman.id,
                deputyChairman.id,
            ).shouldBeFailure(DirectionCreationError.INVALID_DIRECTION_DATA)
        }
    }

    listOf(
        "",
    ).forEach { invalidPath ->
        test("Direction with invalid logo path should not be inserted ($invalidPath)") {
            createDirection(
                validDirectionName,
                validDirectionDescription,
                invalidPath,
                banner.filename,
                chairman.id,
                deputyChairman.id,
            ).shouldBeFailure(DirectionCreationError.INVALID_DIRECTION_DATA)
        }
    }

    listOf(
        "",
    ).forEach { invalidPath ->
        test("Direction with invalid banner path should not be inserted ($invalidPath)") {
            createDirection(
                validDirectionName,
                validDirectionDescription,
                logo.filename,
                invalidPath,
                chairman.id,
                deputyChairman.id,
            ).shouldBeFailure(DirectionCreationError.INVALID_DIRECTION_DATA)
        }
    }

    listOf(
        -1,
    ).forEach { invalidId ->
        test("Direction with invalid chairman id should not be inserted ($invalidId)") {
            createDirection(
                validDirectionName,
                validDirectionDescription,
                logo.filename,
                banner.filename,
                invalidId,
                deputyChairman.id,
            ).shouldBeFailure(DirectionCreationError.INVALID_DIRECTION_DATA)
        }
    }

    listOf(
        -1,
    ).forEach { invalidId ->
        test("Direction with invalid deputy chairman id should not be inserted ($invalidId)") {
            createDirection(
                validDirectionName,
                validDirectionDescription,
                logo.filename,
                banner.filename,
                chairman.id,
                invalidId,
            ).shouldBeFailure(DirectionCreationError.INVALID_DIRECTION_DATA)
        }
    }

    test("There cannot be two directions with the same name") {
        createDirection(
            validDirectionName,
            validDirectionDescription,
            logo.filename,
            banner.filename,
            chairman.id,
            deputyChairman.id,
        ).shouldBeSuccess()

        createDirection(
            validDirectionName,
            validDirectionDescription,
            logo.filename,
            banner.filename,
            chairman.id,
            deputyChairman.id,
        ).shouldBeFailure(DirectionCreationError.NAME_ALREADY_EXISTS)
    }

    test("Unknown db error test for CreateDirection") {
        createDirectionNullName(
            validDirectionName,
            validDirectionDescription,
            logo.filename,
            banner.filename,
            chairman.id,
            deputyChairman.id,
        ).shouldBeFailure(DirectionCreationError.UNKNOWN_DATABASE_ERROR)
    }
})
