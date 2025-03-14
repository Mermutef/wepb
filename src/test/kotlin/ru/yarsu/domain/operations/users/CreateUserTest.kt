package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.config
import ru.yarsu.domain.operations.validEmail
import ru.yarsu.domain.operations.validPass
import ru.yarsu.domain.operations.validUserName

class CreateUserTest : FunSpec({
    val users = mutableListOf<User>()

    beforeEach {
        users.clear()
    }

    val insertUserMock: (name: String, email: String, pass: String, role: Role) -> User? = { name, email, pass, role ->
        val user =
            User(
                id = users.size + 1,
                name,
                email,
                pass,
                role,
            )
        users.add(user)
        user
    }

    val fetchUserByNameMock: (String) -> User? = { userName ->
        users.firstOrNull { it.name == userName }
    }

    val fetchUserByEmailMock: (String) -> User? = { userName ->
        users.firstOrNull { it.email == userName }
    }

    val insertUserNullMock: (name: String, email: String, pass: String, role: Role) -> User? = { _, _, _, _ -> null }
    val fetchUserByNameNullMock: (String) -> User? = { _ -> null }
    val fetchUserByEmailNullMock: (String) -> User? = { _ -> null }

    val createUser = CreateUser(
        insertUserMock,
        fetchUserByNameMock,
        fetchUserByEmailMock,
        config,
    )

    val createUserNullName = CreateUser(
        insertUserNullMock,
        fetchUserByNameNullMock,
        fetchUserByEmailMock,
        config,
    )
    val createUserNullEmail = CreateUser(
        insertUserNullMock,
        fetchUserByNameMock,
        fetchUserByEmailNullMock,
        config,
    )

    test("Valid user can be inserted") {
        createUser(
            validUserName,
            validEmail,
            validPass,
            Role.AUTHORIZED,
        )
            .shouldBeSuccess()
    }

    Role
        .entries
        .minus(Role.ANONYMOUS)
        .forEach { role ->
            test("All valid roles can be inserted ($role)") {
                createUser(
                    validUserName,
                    validEmail,
                    validPass,
                    role,
                ).shouldBeSuccess()
            }
        }

    listOf(
        "",
        "     ",
        "TooManyCharacters".repeat(User.MAX_NAME_LENGTH + 1),
    ).forEach { invalidName ->
        test("User with invalid name should not be inserted ($invalidName)") {
            createUser(
                invalidName,
                validEmail,
                validPass,
                Role.AUTHORIZED,
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
                validUserName,
                invalidEmail,
                validPass,
                Role.AUTHORIZED,
            ).shouldBeFailure(UserCreationError.INVALID_USER_DATA)
        }
    }

    test("User with invalid password should not be inserted") {
        createUser(
            validUserName,
            validEmail,
            "",
            Role.AUTHORIZED,
        ).shouldBeFailure(UserCreationError.INVALID_USER_DATA)
    }

    test("There cannot be two users with the same name") {
        createUser(
            validUserName,
            validEmail,
            validPass,
            Role.AUTHORIZED,
        ).shouldBeSuccess()

        createUser(
            validUserName,
            "1$validEmail",
            validPass,
            Role.AUTHORIZED,
        ).shouldBeFailure(UserCreationError.NAME_ALREADY_EXISTS)
    }

    test("There cannot be two users with the same email") {
        createUser(
            validUserName,
            validEmail,
            validPass,
            Role.AUTHORIZED,
        ).shouldBeSuccess()

        createUser(
            "1$validUserName",
            validEmail,
            validPass,
            Role.AUTHORIZED,
        ).shouldBeFailure(UserCreationError.EMAIL_ALREADY_EXISTS)
    }

    test("Unknown db error test for CreateUser") {
        createUserNullName(
            validUserName,
            validEmail,
            validPass,
            Role.AUTHORIZED,
        ).shouldBeFailure(UserCreationError.UNKNOWN_DATABASE_ERROR)

        createUserNullEmail(
            validUserName,
            validEmail,
            validPass,
            Role.AUTHORIZED,
        ).shouldBeFailure(UserCreationError.UNKNOWN_DATABASE_ERROR)
    }
})
