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

    val fetchUserByLoginMock: (String) -> User? = { userName ->
        users.firstOrNull { it.login == userName }
    }

    val fetchUserByEmailMock: (String) -> User? = { userName ->
        users.firstOrNull { it.email == userName }
    }

    val fetchUserByPhoneMock: (String) -> User? = { userName ->
        users.firstOrNull { it.phoneNumber == userName }
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

    test("User with invalid password should not be inserted") {
        createUser(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            "",
            validVKLink,
            Role.READER,
        ).shouldBeFailure(UserCreationError.INVALID_USER_DATA)
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
    }
})
