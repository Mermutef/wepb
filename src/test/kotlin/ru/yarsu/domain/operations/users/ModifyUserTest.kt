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

    val changeNameMock: (userID: Int, newName: String) -> User? =
        { _, newName -> validAnonymous.copy(name = newName) }

    val changeName = ChangeStringFieldInUser(User.MAX_NAME_LENGTH, User.namePattern, changeNameMock)

    val changeSurnameMock: (userID: Int, newSurname: String) -> User? =
        { _, newSurname -> validAnonymous.copy(surname = newSurname) }

    val changeSurname = ChangeStringFieldInUser(User.MAX_SURNAME_LENGTH, User.namePattern, changeSurnameMock)

    val changeEmailMock: (userID: Int, newEmail: String) -> User? =
        { _, newEmail -> validAnonymous.copy(email = newEmail) }

    val changeEmail = ChangeStringFieldInUser(User.MAX_EMAIL_LENGTH, User.emailPattern, changeEmailMock)

    val changePhoneNumberMock: (userID: Int, newPhone: String) -> User? =
        { _, newPhone -> validAnonymous.copy(phoneNumber = newPhone) }

    val changePhoneNumber =
        ChangeStringFieldInUser(User.MAX_PHONE_NUMBER_LENGTH, User.phonePattern, changePhoneNumberMock)

    val changePasswordMock: (userID: Int, newPassword: String) -> User? =
        { _, newPass -> validAnonymous.copy(password = newPass) }

    val changePassword = ChangePassword(changePasswordMock, User.MAX_PASSWORD_LENGTH, config)

    val changeVKLinkMock: (userID: Int, newVKLink: String) -> User? =
        { _, newVKLink -> validAnonymous.copy(vkLink = newVKLink) }

    val changeVKLink = ChangeStringFieldInUser(User.MAX_VK_LINK_LENGTH, User.vkLinkPattern, changeVKLinkMock)

    val changeNameNullMock: (userID: Int, newName: String) -> User? =
        { _, _ -> null }

    val changeNameNull = ChangeStringFieldInUser(User.MAX_NAME_LENGTH, User.namePattern, changeNameNullMock)

    val changeSurnameNullMock: (userID: Int, newSurname: String) -> User? =
        { _, _ -> null }

    val changeSurnameNull = ChangeStringFieldInUser(User.MAX_SURNAME_LENGTH, User.namePattern, changeSurnameNullMock)

    val changeEmailNullMock: (userID: Int, newEmail: String) -> User? =
        { _, _ -> null }

    val changeEmailNull = ChangeStringFieldInUser(User.MAX_EMAIL_LENGTH, User.emailPattern, changeEmailNullMock)

    val changePhoneNumberNullMock: (userID: Int, newPhone: String) -> User? =
        { _, _ -> null }

    val changePhoneNumberNull = ChangeStringFieldInUser(
        User.MAX_PHONE_NUMBER_LENGTH,
        User.phonePattern,
        changePhoneNumberNullMock
    )

    val changePasswordNullMock: (userID: Int, newPassword: String) -> User? =
        { _, _ -> null }

    val changePasswordNull = ChangePassword(changePasswordNullMock, User.MAX_PASSWORD_LENGTH, config)

    val changeVKLinkNullMock: (userID: Int, newVKLink: String) -> User? =
        { _, _ -> null }

    val changeVKLinkNull = ChangeStringFieldInUser(User.MAX_VK_LINK_LENGTH, User.vkLinkPattern, changeVKLinkNullMock)

    test("Name can be changed to valid name") {
        changeName(validAnonymous, "Вася").shouldBeSuccess().name shouldBe "Вася"
    }

    test("Name cannot be changed to blank name") {
        changeName(validAnonymous, "  \t\n") shouldBeFailure
            FieldInUserChangingError.FIELD_IS_BLANK_OR_EMPTY
    }

    test("Name cannot be changed too long name") {
        changeName(validAnonymous, "a".repeat(User.MAX_NAME_LENGTH + 1)) shouldBeFailure
            FieldInUserChangingError.FIELD_IS_TOO_LONG
    }

    test("Name cannot be changed not matching pattern name") {
        changeName(validAnonymous, "Vasy") shouldBeFailure
            FieldInUserChangingError.FIELD_PATTERN_MISMATCH
    }

    test("Unknown db error test for changeName") {
        changeNameNull(validAnonymous, "Вася") shouldBeFailure
            FieldInUserChangingError.UNKNOWN_CHANGING_ERROR
    }

    test("Surname can be changed to valid surname") {
        changeSurname(validAnonymous, "Васильев").shouldBeSuccess().surname shouldBe "Васильев"
    }

    test("Surname cannot be changed to blank surname") {
        changeSurname(validAnonymous, "  \t\n") shouldBeFailure
            FieldInUserChangingError.FIELD_IS_BLANK_OR_EMPTY
    }

    test("Surname cannot be changed too long surname") {
        changeSurname(validAnonymous, "a".repeat(User.MAX_SURNAME_LENGTH + 1)) shouldBeFailure
            FieldInUserChangingError.FIELD_IS_TOO_LONG
    }

    test("Surname cannot be changed not matching pattern surname") {
        changeSurname(validAnonymous, "Vasiliev") shouldBeFailure
            FieldInUserChangingError.FIELD_PATTERN_MISMATCH
    }

    test("Unknown db error test for changeSurname") {
        changeSurnameNull(validAnonymous, "Васильев") shouldBeFailure
            FieldInUserChangingError.UNKNOWN_CHANGING_ERROR
    }

    test("Email can be changed to valid email") {
        changeEmail(validAnonymous, "1$validEmail").shouldBeSuccess().email shouldBe "1$validEmail"
    }

    test("Email cannot be changed to blank email") {
        changeEmail(validAnonymous, "  \t\n") shouldBeFailure
            FieldInUserChangingError.FIELD_IS_BLANK_OR_EMPTY
    }

    test("Email cannot be changed too long email") {
        changeEmail(validAnonymous, "a".repeat(User.MAX_EMAIL_LENGTH + 1)) shouldBeFailure
            FieldInUserChangingError.FIELD_IS_TOO_LONG
    }

    test("Email cannot be changed not matching pattern Email") {
        changeEmail(validAnonymous, "invalid") shouldBeFailure
            FieldInUserChangingError.FIELD_PATTERN_MISMATCH
    }

    test("Unknown db error test for changeEmail") {
        changeEmailNull(validAnonymous, "1$validEmail") shouldBeFailure
            FieldInUserChangingError.UNKNOWN_CHANGING_ERROR
    }

    test("PhoneNumber can be changed to valid phoneNumber") {
        changePhoneNumber(validAnonymous, "79000000000").shouldBeSuccess().phoneNumber shouldBe "79000000000"
    }

    test("PhoneNumber cannot be changed to blank phoneNumber") {
        changePhoneNumber(validAnonymous, "  \t\n") shouldBeFailure
            FieldInUserChangingError.FIELD_IS_BLANK_OR_EMPTY
    }

    test("PhoneNumber cannot be changed too long phoneNumber") {
        changePhoneNumber(validAnonymous, "a".repeat(User.MAX_PHONE_NUMBER_LENGTH + 1)) shouldBeFailure
            FieldInUserChangingError.FIELD_IS_TOO_LONG
    }

    test("PhoneNumber cannot be changed not matching pattern phoneNumber") {
        changePhoneNumber(validAnonymous, "6900000000") shouldBeFailure
            FieldInUserChangingError.FIELD_PATTERN_MISMATCH
    }

    test("Unknown db error test for changePhoneNumber") {
        changePhoneNumberNull(validAnonymous, "79000000000") shouldBeFailure
            FieldInUserChangingError.UNKNOWN_CHANGING_ERROR
    }

    test("Password can be changed to valid password") {
        changePassword(validAnonymous, "valid").shouldBeSuccess().password shouldBe hasher.hash("valid")
    }

    test("Password cannot be changed to blank password") {
        changePassword(validAnonymous, "  \t\n") shouldBeFailure
            PasswordChangingError.PASSWORD_IS_BLANK_OR_EMPTY
    }

    test("Password cannot be changed too long password") {
        changePassword(validAnonymous, "a".repeat(User.MAX_PASSWORD_LENGTH + 1)) shouldBeFailure
            PasswordChangingError.PASSWORD_IS_TOO_LONG
    }

    test("Unknown db error test for changePassword") {
        changePasswordNull(validAnonymous, "valid") shouldBeFailure
            PasswordChangingError.UNKNOWN_CHANGING_ERROR
    }

    test("VKLink can be changed to valid vkLink") {
        changeVKLink(validAnonymous, "${validVKLink}1").shouldBeSuccess().vkLink shouldBe "${validVKLink}1"
    }

    test("VKLink cannot be changed to blank vkLink") {
        changeVKLink(validAnonymous, "  \t\n") shouldBeFailure
            FieldInUserChangingError.FIELD_IS_BLANK_OR_EMPTY
    }

    test("VKLink cannot be changed too long vkLink") {
        changeVKLink(validAnonymous, "a".repeat(User.MAX_VK_LINK_LENGTH + 1)) shouldBeFailure
            FieldInUserChangingError.FIELD_IS_TOO_LONG
    }

    test("VKLink cannot be changed not matching pattern vkLink") {
        changeVKLink(validAnonymous, "invalid_vk_link") shouldBeFailure
            FieldInUserChangingError.FIELD_PATTERN_MISMATCH
    }

    test("Unknown db error test for changeVKLink") {
        changeVKLinkNull(validAnonymous, "${validVKLink}1") shouldBeFailure
            FieldInUserChangingError.UNKNOWN_CHANGING_ERROR
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
