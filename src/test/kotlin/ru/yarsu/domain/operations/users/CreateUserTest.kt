package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.db.validLogin
import ru.yarsu.db.validUserSurname
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.config
import ru.yarsu.domain.operations.validEmail
import ru.yarsu.domain.operations.validName
import ru.yarsu.domain.operations.validPass
import ru.yarsu.domain.operations.validPhoneNumber
import ru.yarsu.domain.operations.validVKLink

class CreateUserTest : FunSpec({
    val users = mutableListOf<User>()

    beforeEach {
        users.clear()
    }

    val insertUserMock: (
        name: String,
        surname: String,
        login: String,
        email: String,
        phoneNumber: String,
        pass: String,
        vkLink: String?,
        role: Role,
    ) ->
    User? = { name, surname, login, email, phoneNumber, pass, vkLink, role ->
        val user =
            User(
                id = users.size + 1,
                name,
                surname,
                login,
                email,
                phoneNumber,
                pass,
                vkLink,
                role,
            )
        users.add(user)
        user
    }

    val fetchUserByLoginMock: (String) -> User? = { userLogin ->
        users.firstOrNull { it.login == userLogin }
    }

    val fetchUserByEmailMock: (String) -> User? = { userEmail ->
        users.firstOrNull { it.email == userEmail }
    }

    val fetchUserByPhoneMock: (String) -> User? = { userPhone ->
        users.firstOrNull { it.phoneNumber == userPhone }
    }

    val insertUserNullMock: (
        name: String,
        surname: String,
        login: String,
        email: String,
        phoneNumber: String,
        pass: String,
        vkLink: String?,
        role: Role,
    ) -> User? = { _, _, _, _, _, _, _, _ -> null }
    val fetchUserByNameNullMock: (String) -> User? = { _ -> null }
    val fetchUserByEmailNullMock: (String) -> User? = { _ -> null }
    val fetchUserByPhoneNullMock: (String) -> User? = { _ -> null }

    val createUser = CreateUser(
        insertUserMock,
        fetchUserByLoginMock,
        fetchUserByEmailMock,
        fetchUserByPhoneMock,
        config,
    )

    val createUserNullName = CreateUser(
        insertUserNullMock,
        fetchUserByNameNullMock,
        fetchUserByEmailMock,
        fetchUserByPhoneMock,
        config,
    )

    val createUserNullEmail = CreateUser(
        insertUserNullMock,
        fetchUserByLoginMock,
        fetchUserByEmailNullMock,
        fetchUserByPhoneMock,
        config,
    )

    val createUserNullPhone = CreateUser(
        insertUserNullMock,
        fetchUserByLoginMock,
        fetchUserByEmailMock,
        fetchUserByPhoneNullMock,
        config,
    )

    test("Valid user can be inserted") {
        createUser(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER,
        )
            .shouldBeSuccess()
    }

    Role
        .entries
        .minus(Role.ANONYMOUS)
        .forEach { role ->
            test("All valid roles can be inserted ($role)") {
                createUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber,
                    validPass,
                    validVKLink,
                    role,
                ).shouldBeSuccess()
            }
        }

    listOf(
        "",
        "Ivan",
        "Иван".repeat(User.MAX_NAME_LENGTH + 1),
    ).forEach { invalidName ->
        test("User with invalid name should not be inserted ($invalidName)") {
            createUser(
                invalidName,
                validUserSurname,
                validLogin,
                validEmail,
                validPhoneNumber,
                validPass,
                validVKLink,
                Role.READER,
            ).shouldBeFailure(UserCreationError.INVALID_USER_DATA)
        }
    }

    listOf(
        "",
        "Ivanov",
        "Иванов".repeat(User.MAX_SURNAME_LENGTH + 1),
    ).forEach { invalidUserSurname ->
        test("User with invalid surname should not be inserted ($invalidUserSurname)") {
            createUser(
                validName,
                invalidUserSurname,
                validLogin,
                validEmail,
                validPhoneNumber,
                validPass,
                validVKLink,
                Role.READER,
            ).shouldBeFailure(UserCreationError.INVALID_USER_DATA)
        }
    }

    listOf(
        "",
        "     ",
        "TooManyCharacters".repeat(User.MAX_LOGIN_LENGTH + 1),
    ).forEach { invalidLogin ->
        test("User with invalid login should not be inserted ($invalidLogin)") {
            createUser(
                validName,
                validUserSurname,
                invalidLogin,
                validEmail,
                validPhoneNumber,
                validPass,
                validVKLink,
                Role.READER,
            ).shouldBeFailure(UserCreationError.INVALID_USER_DATA)
        }
    }

    listOf(
        "",
        "TooManyCharacters".repeat(User.MAX_EMAIL_LENGTH + 1),
        "invalid@gmailcom",
        "invalidgmail.com",
        "invalidgmailcom",
    ).forEach { invalidEmail ->
        test("User with invalid email should not be inserted ($invalidEmail)") {
            createUser(
                validName,
                validUserSurname,
                validLogin,
                invalidEmail,
                validPhoneNumber,
                validPass,
                validVKLink,
                Role.READER,
            ).shouldBeFailure(UserCreationError.INVALID_USER_DATA)
        }
    }

    listOf(
        "",
        "99898989898",
        "89".repeat(User.MAX_PHONE_NUMBER_LENGTH + 1),
    ).forEach { invalidPhoneNumber ->
        test("User with invalid phone number should not be inserted ($invalidPhoneNumber)") {
            createUser(
                validName,
                validUserSurname,
                validLogin,
                validEmail,
                invalidPhoneNumber,
                validPass,
                validVKLink,
                Role.READER,
            ).shouldBeFailure(UserCreationError.INVALID_USER_DATA)
        }
    }

    listOf(
        "",
        "a".repeat(User.MAX_PASSWORD_LENGTH + 1),
    ).forEach { invalidPassword ->
        test("User with invalid password should not be inserted ($invalidPassword)") {
            createUser(
                validName,
                validUserSurname,
                validLogin,
                validEmail,
                validPhoneNumber,
                invalidPassword,
                validVKLink,
                Role.READER,
            ).shouldBeFailure(UserCreationError.INVALID_USER_DATA)
        }
    }

    listOf(
        "",
        "aaaaaaa",
        "a".repeat(User.MAX_VK_LINK_LENGTH + 1),
    ).forEach { invalidVKLink ->
        test("User with invalid vk link should not be inserted ($invalidVKLink)") {
            createUser(
                validName,
                validUserSurname,
                validLogin,
                validEmail,
                validPhoneNumber,
                validPass,
                invalidVKLink,
                Role.READER,
            ).shouldBeFailure(UserCreationError.INVALID_USER_DATA)
        }
    }

    test("There cannot be two users with the same login") {
        createUser(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER,
        ).shouldBeSuccess()

        createUser(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER,
        ).shouldBeFailure(UserCreationError.LOGIN_ALREADY_EXISTS)
    }

    test("There cannot be two users with the same email") {
        createUser(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER,
        ).shouldBeSuccess()

        createUser(
            validName,
            validUserSurname,
            "1$validLogin",
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER,
        ).shouldBeFailure(UserCreationError.EMAIL_ALREADY_EXISTS)
    }

    test("There cannot be two users with the same phone number") {
        createUser(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER,
        ).shouldBeSuccess()

        createUser(
            validName,
            validUserSurname,
            "1$validLogin",
            "1$validEmail",
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER,
        ).shouldBeFailure(UserCreationError.PHONE_ALREADY_EXISTS)
    }

    test("Unknown db error test for CreateUser") {
        createUserNullName(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER,
        ).shouldBeFailure(UserCreationError.UNKNOWN_DATABASE_ERROR)

        createUserNullEmail(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER,
        ).shouldBeFailure(UserCreationError.UNKNOWN_DATABASE_ERROR)

        createUserNullPhone(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER,
        ).shouldBeFailure(UserCreationError.UNKNOWN_DATABASE_ERROR)
    }

    test("There can be user with null vk link") {
        createUser(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            null,
            Role.READER,
        ).shouldBeSuccess()
    }
})
