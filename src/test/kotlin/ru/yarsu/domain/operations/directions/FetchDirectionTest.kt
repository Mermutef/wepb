package ru.yarsu.domain.operations.directions

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
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

class FetchDirectionTest : FunSpec({
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

    val validDirection = Direction(
        0,
        validDirectionName,
        validDirectionDescription,
        logo.filename,
        banner.filename,
        chairman.id,
        deputyChairman.id,
    )

    val directions = listOf(validDirection)

    val fetchDirectionByIDMock: (Int) -> Direction? = { directionID ->
        directions.firstOrNull { it.id == directionID }
    }

    val fetchChairmanIDDirectionIDMock: (Int) -> Int? = { directionID ->
        directions.firstOrNull { it.id == directionID }?.chairmanId
    }

    val fetchDeputyChairmanIDByDirectionIDMock: (Int) -> Int? = { directionID ->
        directions.firstOrNull { it.id == directionID }?.deputyChairmanId
    }

    val fetchDirectionNameAndLogoByIDMock: (Int) -> Direction? = { directionID ->
        directions.firstOrNull { it.id == directionID }
    }

    val fetchAllDirectionsMock: () -> List<Direction> = { directions }

    val fetchAllDirectionsNameAndLogoMock: () -> List<Direction> = { directions }

    val fetchDirectionByIDNullMock: (Int) -> Direction? = { _ -> null }
    val fetchChairmanIDDirectionIDNullMock: (Int) -> Int? = { _ -> null }
    val fetchDeputyChairmanIDByDirectionIDNullMock: (Int) -> Int? = { _ -> null }
    val fetchDirectionNameAndLogoByIDNullMock: (Int) -> Direction? = { _ -> null }

    val fetchDirectionByID = FetchDirectionByID(fetchDirectionByIDMock)
    val fetchChairmanIDByDirectionID = FetchChairmanIDByDirectionID(fetchChairmanIDDirectionIDMock)
    val fetchDeputyChairmanIDByDirectionID = FetchDeputyChairmanIDByDirectionID(fetchDeputyChairmanIDByDirectionIDMock)
    val fetchDirectionNameAndLogoByID = FetchDirectionNameAndLogoByID(fetchDirectionNameAndLogoByIDMock)
    val fetchAllDirections = FetchAllDirections(fetchAllDirectionsMock)
    val fetchAllDirectionsNameAndLogo = FetchAllDirectionsNameAndLogo(fetchAllDirectionsNameAndLogoMock)

    val fetchDirectionByIdNull = FetchDirectionByID(fetchDirectionByIDNullMock)
    val fetchChairmanIDByDirectionIDNull = FetchChairmanIDByDirectionID(fetchChairmanIDDirectionIDNullMock)
    val fetchDeputyChairmanIDByDirectionIDNull =
        FetchDeputyChairmanIDByDirectionID(fetchDeputyChairmanIDByDirectionIDNullMock)
    val fetchDirectionNameAndLogoByIDNull = FetchDirectionNameAndLogoByID(fetchDirectionNameAndLogoByIDNullMock)

    test("Direction can be fetched by his ID") {
        fetchDirectionByID(validDirection.id).shouldBeSuccess()
    }

    test("Chairman ID can be fetched by his direction ID") {
        fetchChairmanIDByDirectionID(validDirection.id).shouldBeSuccess()
    }

    test("Deputy chairman ID can be fetched by his direction ID") {
        fetchDeputyChairmanIDByDirectionID(validDirection.id).shouldBeSuccess()
    }

    test("Direction name and logo can be fetched by his ID") {
        fetchDirectionNameAndLogoByID(validDirection.id).shouldBeSuccess()
    }

    test("FetchAllDirections should return list of directions") {
        fetchAllDirections().shouldBeSuccess().shouldHaveSize(1)
    }

    test("FetchAllDirectionsNameAndLogo should return list of directions") {
        fetchAllDirectionsNameAndLogo().shouldBeSuccess().shouldHaveSize(1)
    }

    listOf(
        validDirection.id - 1,
        validDirection.id + 1,
    ).forEach { directionID ->
        test("Direction can't be fetched by invalid ID == $directionID") {
            fetchDirectionByID(directionID)
                .shouldBeFailure(DirectionFetchingError.NO_SUCH_DIRECTION)
            fetchDirectionByIdNull(directionID)
                .shouldBeFailure(DirectionFetchingError.NO_SUCH_DIRECTION)
        }
    }

    listOf(
        validDirection.id - 1,
        validDirection.id + 1,
    ).forEach { directionID ->
        test("Chairman ID can't be fetched by invalid direction ID == $directionID") {
            fetchChairmanIDByDirectionID(directionID)
                .shouldBeFailure(DirectionFetchingError.NO_SUCH_ID)
            fetchChairmanIDByDirectionIDNull(directionID)
                .shouldBeFailure(DirectionFetchingError.NO_SUCH_ID)
        }
    }

    listOf(
        validDirection.id - 1,
        validDirection.id + 1,
    ).forEach { directionID ->
        test("Deputy chairman ID can't be fetched by invalid direction ID == $directionID") {
            fetchDeputyChairmanIDByDirectionID(directionID)
                .shouldBeFailure(DirectionFetchingError.NO_SUCH_ID)
            fetchDeputyChairmanIDByDirectionIDNull(directionID)
                .shouldBeFailure(DirectionFetchingError.NO_SUCH_ID)
        }
    }

    listOf(
        validDirection.id - 1,
        validDirection.id + 1,
    ).forEach { directionID ->
        test("Direction name and logo can't be fetched by invalid ID == $directionID") {
            fetchDirectionNameAndLogoByID(directionID)
                .shouldBeFailure(DirectionFetchingError.NO_SUCH_DIRECTION)
            fetchDirectionNameAndLogoByIDNull(directionID)
                .shouldBeFailure(DirectionFetchingError.NO_SUCH_DIRECTION)
        }
    }
})
