package ru.yarsu.domain.operations.directions

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
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

class ModifyDirectionTest : FunSpec({

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

    val validDirectionUpdate = Direction(
        0,
        validDirectionName + " направление",
        validDirectionDescription + " это круто",
        logo.filename + "a",
        banner.filename + "a",
        deputyChairman.id,
        chairman.id,
    )

    val directions = mutableListOf<Direction>()

    val removeDirectionMock: (directionID: Int) -> Unit =
        { id -> directions.removeIf { it.id == id } }

    val removeDirection = RemoveDirection(removeDirectionMock)

    val updateDirectionMock: (
        directionId: Int,
        newName: String,
        newDescription: String,
        newLogoPath: String,
        newBannerPath: String,
        newChairmanId: Int,
        newDeputyChairmanId: Int,
    ) -> Direction? = {
            _,
            newName,
            newDescription,
            newLogoPath,
            newBannerPath,
            newChairmanId,
            newDeputyChairmanId,
        ->
        validDirection
            .copy(
                name = newName,
                description = newDescription,
                logoPath = newLogoPath,
                bannerPath = newBannerPath,
                chairmanId = newChairmanId,
                deputyChairmanId = newDeputyChairmanId
            )
    }

    val updateDirection = UpdateDirection(updateDirectionMock)

    val changeChairmanMock: (directionId: Int, newChairmanId: Int) -> Direction? =
        { _, newChairmanId -> validDirection.copy(chairmanId = newChairmanId) }

    val changeChairman = ChangeChairman(changeChairmanMock)

    val changeDeputyChairmanMock: (directionId: Int, newDeputyChairmanId: Int) -> Direction? =
        { _, newDeputyChairmanId -> validDirection.copy(deputyChairmanId = newDeputyChairmanId) }

    val changeDeputyChairman = ChangeDeputyChairman(changeDeputyChairmanMock)

    val updateDirectionNullMock: (
        directionId: Int,
        newName: String,
        newDescription: String,
        newLogoPath: String,
        newBannerPath: String,
        newChairmanId: Int,
        newDeputyChairmanId: Int,
    ) -> Direction? = { _, _, _, _, _, _, _ -> null }

    val updateDirectionNull = UpdateDirection(updateDirectionNullMock)

    val changeChairmanNullMock: (directionId: Int, newChairmanId: Int) -> Direction? =
        { _, _ -> null }

    val changeChairmanNull = ChangeChairman(changeChairmanNullMock)

    val changeDeputyChairmanNullMock: (directionId: Int, newDeputyChairmanId: Int) -> Direction? =
        { _, _ -> null }

    val changeDeputyChairmanNull = ChangeDeputyChairman(changeDeputyChairmanNullMock)

    beforeEach {
        directions.removeAll(directions)
    }

    test("Name can be changed to valid name") {
        updateDirection(
            validDirection,
            validDirectionName + " направление",
            validDirectionDescription + " это круто",
            logo.filename + "a",
            banner.filename + "a",
            deputyChairman.id,
            chairman.id,
        ).shouldBeSuccess() shouldBe validDirectionUpdate
    }

    test("Name cannot be changed to blank name") {
        updateDirection(
            validDirection,
            "  \t\n",
            validDirectionDescription + "а",
            logo.filename + "a",
            banner.filename + "a",
            chairman.id + 1,
            deputyChairman.id + 1,
        ) shouldBeFailure DirectionUpdateError.INVALID_DIRECTION_DATA
    }

    test("Name cannot be changed too long name") {
        updateDirection(
            validDirection,
            "a".repeat(Direction.MAX_NAME_LENGTH + 1),
            validDirectionDescription + "а",
            logo.filename + "a",
            banner.filename + "a",
            chairman.id + 1,
            deputyChairman.id + 1,
        ) shouldBeFailure DirectionUpdateError.INVALID_DIRECTION_DATA
    }

    test("Name cannot be changed not matching pattern name") {
        updateDirection(
            validDirection,
            "sport",
            validDirectionDescription + "а",
            logo.filename + "a",
            banner.filename + "a",
            chairman.id + 1,
            deputyChairman.id + 1,
        ) shouldBeFailure DirectionUpdateError.INVALID_DIRECTION_DATA
    }

    test("Description cannot be changed to blank description") {
        updateDirection(
            validDirection,
            validDirectionName,
            "  \t\n",
            logo.filename + "a",
            banner.filename + "a",
            chairman.id + 1,
            deputyChairman.id + 1,
        ) shouldBeFailure DirectionUpdateError.INVALID_DIRECTION_DATA
    }

    test("Description cannot be changed not matching pattern description") {
        updateDirection(
            validDirection,
            validDirectionName,
            "sport",
            logo.filename + "a",
            banner.filename + "a",
            chairman.id + 1,
            deputyChairman.id + 1,
        ) shouldBeFailure DirectionUpdateError.INVALID_DIRECTION_DATA
    }

    test("LogoPath cannot be changed to blank logoPath") {
        updateDirection(
            validDirection,
            validDirectionName,
            validDirectionDescription,
            "  \t\n",
            banner.filename + "a",
            chairman.id + 1,
            deputyChairman.id + 1,
        ) shouldBeFailure DirectionUpdateError.INVALID_DIRECTION_DATA
    }

    test("BannerPath cannot be changed to blank bannerPath") {
        updateDirection(
            validDirection,
            validDirectionName,
            validDirectionDescription,
            logo.filename + "a",
            "  \t\n",
            chairman.id + 1,
            deputyChairman.id + 1,
        ) shouldBeFailure DirectionUpdateError.INVALID_DIRECTION_DATA
    }

    test("ChairmanId cannot be changed to invalid chairmanId") {
        updateDirection(
            validDirection,
            validDirectionName,
            validDirectionDescription,
            logo.filename + "a",
            banner.filename,
            -1,
            deputyChairman.id + 1,
        ) shouldBeFailure DirectionUpdateError.INVALID_DIRECTION_DATA
    }

    test("DeputyChairmanId cannot be changed to invalid deputyChairmanId") {
        updateDirection(
            validDirection,
            validDirectionName,
            validDirectionDescription,
            logo.filename + "a",
            banner.filename,
            chairman.id,
            -1,
        ) shouldBeFailure DirectionUpdateError.INVALID_DIRECTION_DATA
    }

    test("Unknown db error test for updateDirection") {
        updateDirectionNull(
            validDirection,
            validDirectionName,
            validDirectionDescription,
            logo.filename,
            banner.filename,
            chairman.id,
            deputyChairman.id,
        ) shouldBeFailure DirectionUpdateError.UNKNOWN_UPDATING_ERROR
    }

    test("ChairmanId can be changed to valid chairmanId") {
        changeChairman(validDirection, 2)
            .shouldBeSuccess().chairmanId shouldBe 2
    }

    test("ChairmanId cannot be changed to invalid chairmanId") {
        changeChairman(
            validDirection,
            -1
        ) shouldBeFailure ChairmanChangingError.INCORRECT_ID
    }

    test("Unknown db error test for changeChairman") {
        changeChairmanNull(
            validDirection,
            2
        ) shouldBeFailure ChairmanChangingError.UNKNOWN_CHANGING_ERROR
    }

    test("DeputyChairmanId can be changed to valid deputyChairmanId") {
        changeDeputyChairman(validDirection, 2)
            .shouldBeSuccess().deputyChairmanId shouldBe 2
    }

    test("DeputyChairmanId cannot be changed to invalid deputyChairmanId") {
        changeDeputyChairman(
            validDirection,
            -1
        ) shouldBeFailure DeputyChairmanChangingError.INCORRECT_ID
    }

    test("Unknown db error test for changeDeputyChairman") {
        changeDeputyChairmanNull(
            validDirection,
            2
        ) shouldBeFailure DeputyChairmanChangingError.UNKNOWN_CHANGING_ERROR
    }

    test("Remove media should return success if filename is valid") {
        directions.add(validDirection)
        removeDirection(validDirection).shouldBeSuccess()
    }
})
