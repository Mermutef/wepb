package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import ru.yarsu.db.validLogin
import ru.yarsu.db.validUserSurname
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.validEmail
import ru.yarsu.domain.operations.validName
import ru.yarsu.domain.operations.validPass
import ru.yarsu.domain.operations.validPhoneNumber
import ru.yarsu.domain.operations.validVKLink

class FetchUserTest : FunSpec({
    val validUser = User(
        0,
        validName,
        validUserSurname,
        validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.READER
    )

    val readerUsers = listOf(
        User(
            1,
            "user1",
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER
        ),
        User(
            2,
            "user2",
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.READER
        ),
    )

    val moders = listOf(
        User(
            3,
            "moder1",
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.MODERATOR
        ),
        User(
            4,
            "moder2",
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.MODERATOR
        )
    )
    val users = listOf(validUser)
    var usersForFetchByRole: List<User> = emptyList()

    val fetchUserByIDMock: (Int) -> User? = { userID ->
        users.firstOrNull { it.id == userID }
    }
    val fetchUserByEmailMock: (String) -> User? = { email ->
        users.firstOrNull { it.email == email }
    }
    val fetchUserByNameMock: (String) -> User? = { userName ->
        users.firstOrNull { it.name == userName }
    }
    val fetchUsersByRoleMock: (Role) -> List<User> = { userRole ->
        usersForFetchByRole.filter { it.role == userRole }
    }
    val fetchAllUsersMock: () -> List<User> = { users }

    val fetchUserByIDNullMock: (Int) -> User? = { _ -> null }
    val fetchUserByNameNullMock: (String) -> User? = { _ -> null }
    val fetchUserByEmailNullMock: (String) -> User? = { _ -> null }

    val fetchUserByID = FetchUserByID(fetchUserByIDMock)
    val fetchUserByName = FetchUserByLogin(fetchUserByNameMock)
    val fetchUserByEmail = FetchUserByEmail(fetchUserByEmailMock)
    val fetchAllUsers = FetchAllUsers(fetchAllUsersMock)
    val fetchUserByIdNull = FetchUserByID(fetchUserByIDNullMock)
    val fetchUserByNameNull = FetchUserByLogin(fetchUserByNameNullMock)
    val fetchUserByEmailNull = FetchUserByEmail(fetchUserByEmailNullMock)

    val fetchUsersByRole = FetchUsersByRole(fetchUsersByRoleMock)

    test("User can be fetched by his ID") {
        fetchUserByID(validUser.id).shouldBeSuccess()
    }

    listOf(
        validUser.id - 1,
        validUser.id + 1,
    ).forEach { userID ->
        test("User can't be fetched by invalid ID == $userID") {
            fetchUserByID(userID)
                .shouldBeFailure(UserFetchingError.NO_SUCH_USER)
            fetchUserByIdNull(userID)
                .shouldBeFailure(UserFetchingError.NO_SUCH_USER)
        }
    }

    test("User can be fetched by his name") {
        fetchUserByName(validUser.name).shouldBeSuccess()
    }

    test("User can be fetched by his email") {
        fetchUserByEmail(validUser.email).shouldBeSuccess()
    }

    listOf(
        "",
        "    ",
    ).forEach { name ->
        test("User can't be fetched by invalid name == $name") {
            fetchUserByName(name)
                .shouldBeFailure(UserFetchingError.NO_SUCH_USER)
            fetchUserByNameNull(name)
                .shouldBeFailure(UserFetchingError.NO_SUCH_USER)
        }
    }

    listOf(
        "",
        "tenCharact".repeat(11),
        "invalid@gmailcom",
        "invalidgmail.com",
        "invalidgmailcom",
    ).forEach { invalidEmail ->
        test("User can't be fetched by invalid email == $invalidEmail") {
            fetchUserByEmail(invalidEmail)
                .shouldBeFailure(UserFetchingError.NO_SUCH_USER)
            fetchUserByEmailNull(invalidEmail)
                .shouldBeFailure(UserFetchingError.NO_SUCH_USER)
        }
    }

    test("FetchAllUsers should return list of users") {
        fetchAllUsers().shouldBeSuccess().shouldHaveSize(1)
    }

    listOf(
        Triple("no one user", emptyList(), emptyList()),
        Triple("only READER", readerUsers, emptyList()),
        Triple("only moders", moders, moders),
        Triple("moders and READER", readerUsers + moders, moders),
    ).forEach { triple ->
        test(
            "FetchUsersByRole should return list of all" +
                "users only with Role == ${Role.MODERATOR} when has ${triple.first}"
        ) {
            usersForFetchByRole = triple.second
            fetchUsersByRole(Role.MODERATOR)
                .shouldBeSuccess()
                .shouldHaveSize(triple.third.size)
                .shouldContainExactlyInAnyOrder(triple.third)
        }
    }
})
