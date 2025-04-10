package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.db.validLogin
import ru.yarsu.db.validUserSurname
import ru.yarsu.domain.accounts.PasswordHasher
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.config
import ru.yarsu.domain.operations.validEmail
import ru.yarsu.domain.operations.validName
import ru.yarsu.domain.operations.validPass
import ru.yarsu.domain.operations.validPhoneNumber
import ru.yarsu.domain.operations.validVKLink

class ModifyUserTest : FunSpec({

    val validAnonymous = User(
        1,
        validName,
        validUserSurname,
        validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.ANONYMOUS
    )
    val hasher = PasswordHasher(config.authConfig)

    val changePasswordMock: (userID: Int, newPassword: String) -> User? =
        { _, newPass -> validAnonymous.copy(password = newPass) }

    val changePassword = ChangePassword(changePasswordMock, config)

    val changePasswordNullMock: (userID: Int, newPassword: String) -> User? =
        { _, newPass -> null }

    val changePasswordNull = ChangePassword(changePasswordNullMock, config)

    test("Password can be changed to valid password") {
        changePassword(validAnonymous, "valid").shouldBeSuccess().password shouldBe hasher.hash("valid")
    }

    test("Password cannot be changed to empty password") {
        changePassword(validAnonymous, "").shouldBeFailure(PasswordChangingError.PASSWORD_IS_BLANK_OR_EMPTY)
    }

    test("Password cannot be changed to blank password") {
        changePassword(validAnonymous, "  \t\n") shouldBeFailure
            PasswordChangingError.PASSWORD_IS_BLANK_OR_EMPTY
    }

    test("Unknown db error test for changePassword") {
        changePasswordNull(validAnonymous, "valid") shouldBeFailure
            PasswordChangingError.UNKNOWN_CHANGING_ERROR
    }

    val validReader = User(
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

    val validWriter = User(
        1,
        validName,
        validUserSurname,
        validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.WRITER,
    )

    val validModerator = User(
        1,
        validName,
        validUserSurname,
        validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.MODERATOR,
    )

    val makeReaderMock: (user: User, role: Role) -> User? = { user, _ ->
        User(
            user.id,
            user.name,
            user.surname,
            user.login,
            user.email,
            user.phoneNumber,
            user.password,
            user.vkLink,
            Role.READER,
        )
    }

    val makeReaderNullMock: (user: User, role: Role) -> User? = { _, _ -> null }

    val makeWriterMock: (user: User, role: Role) -> User? = { user, _ ->
        User(
            user.id,
            user.name,
            user.surname,
            user.login,
            user.email,
            user.phoneNumber,
            user.password,
            user.vkLink,
            Role.WRITER,
        )
    }
    val makeWriterNullMock: (user: User, role: Role) -> User? = { _, _ -> null }

    val makeModeratorMock: (user: User, role: Role) -> User? = { user, _ ->
        User(
            user.id,
            user.name,
            user.surname,
            user.login,
            user.email,
            user.phoneNumber,
            user.password,
            user.vkLink,
            Role.MODERATOR,
        )
    }
    val makeModeratorNullMock: (user: User, role: Role) -> User? = { _, _ -> null }

    val makeReader = RoleChanger(
        Role.READER,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_READER,
        makeReaderMock,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR
    )
    val makeWriter = RoleChanger(
        Role.WRITER,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_WRITER,
        makeWriterMock,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR
    )
    val makeModerator = RoleChanger(
        Role.MODERATOR,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_MODERATOR,
        makeModeratorMock,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR
    )
    val makeReaderNull = RoleChanger(
        Role.READER,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_READER,
        makeReaderNullMock,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR
    )
    val makeWriterNull = RoleChanger(
        Role.WRITER,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_WRITER,
        makeWriterNullMock,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR
    )
    val makeModeratorNull = RoleChanger(
        Role.MODERATOR,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_MODERATOR,
        makeModeratorNullMock,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR
    )

    test("Anonymous role can be changed to reader") {
        makeReader(validAnonymous).shouldBeSuccess().role shouldBe Role.READER
    }

    test("Anonymous role can be changed to writer") {
        makeWriter(validAnonymous).shouldBeSuccess().role shouldBe Role.WRITER
    }

    test("Anonymous role can be changed to moderator") {
        makeModerator(validAnonymous).shouldBeSuccess().role shouldBe Role.MODERATOR
    }

    test("Reader role can be changed to writer") {
        makeWriter(validReader).shouldBeSuccess().role shouldBe Role.WRITER
    }

    test("Reader role can be changed to moderator") {
        makeModerator(validReader).shouldBeSuccess().role shouldBe Role.MODERATOR
    }

    test("Reader role cannot be changed to reader") {
        makeReader(validReader) shouldBeFailure MakeRoleError.IS_ALREADY_READER
    }

    test("Writer role can be changed to reader") {
        makeReader(validWriter).shouldBeSuccess().role shouldBe Role.READER
    }

    test("Writer role can be changed to moderator") {
        makeModerator(validWriter).shouldBeSuccess().role shouldBe Role.MODERATOR
    }

    test("Writer role cannot be changed to writer") {
        makeWriter(validWriter) shouldBeFailure MakeRoleError.IS_ALREADY_WRITER
    }

    test("Moderator role can be changed to reader") {
        makeReader(validModerator).shouldBeSuccess().role shouldBe Role.READER
    }

    test("Moderator role can be changed to writer") {
        makeWriter(validModerator).shouldBeSuccess().role shouldBe Role.WRITER
    }

    test("Moderator role cannot be changed to moderator") {
        makeModerator(validModerator) shouldBeFailure MakeRoleError.IS_ALREADY_MODERATOR
    }

    test("Unknown db error test for makeReader") {
        makeReaderNull(validAnonymous) shouldBeFailure
            MakeRoleError.UNKNOWN_DATABASE_ERROR
    }

    test("Unknown db error test for makeWriter") {
        makeWriterNull(validAnonymous) shouldBeFailure
            MakeRoleError.UNKNOWN_DATABASE_ERROR
    }

    test("Unknown db error test for makeModerator") {
        makeModeratorNull(validAnonymous) shouldBeFailure
            MakeRoleError.UNKNOWN_DATABASE_ERROR
    }
})
