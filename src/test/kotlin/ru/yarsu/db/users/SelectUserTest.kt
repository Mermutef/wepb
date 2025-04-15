package ru.yarsu.db.users

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.appConfiguredPasswordHasher
import ru.yarsu.db.validEmail
import ru.yarsu.db.validLogin
import ru.yarsu.db.validName
import ru.yarsu.db.validPass
import ru.yarsu.db.validPhoneNumber
import ru.yarsu.db.validSecondPhoneNumber
import ru.yarsu.db.validUserSurname
import ru.yarsu.db.validVKLink
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User

class SelectUserTest : TestcontainerSpec({ context ->
    val userOperations = UserOperations(context)
    lateinit var insertedUser: User
    beforeEach {
        insertedUser =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber,
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.READER,
                ).shouldNotBeNull()
    }

    test("There is only user by default") {
        userOperations
            .selectAllUsers()
            .shouldNotBeNull()
            .size
            .shouldBe(1)
    }

    test("If two users were added, there should be exactly two users and general admin") {

        // already added one

        userOperations
            .insertUser(
                "John",
                validUserSurname,
                "reader2$validLogin",
                "reader2$validEmail",
                validSecondPhoneNumber,
                "pass",
                validVKLink,
                Role.MODERATOR,
            )

        userOperations
            .selectAllUsers()
            .shouldNotBeNull()
            .shouldHaveSize(2)
    }

    test("User can be fetched by valid ID") {
        val fetchedUser =
            userOperations
                .selectUserByID(insertedUser.id)
                .shouldNotBeNull()

        fetchedUser.id.shouldBe(insertedUser.id)
        fetchedUser.name.shouldBe(insertedUser.name)
        fetchedUser.surname.shouldBe(insertedUser.surname)
        fetchedUser.login.shouldBe(insertedUser.login)
        fetchedUser.email.shouldBe(insertedUser.email)
        fetchedUser.phoneNumber.shouldBe(insertedUser.phoneNumber)
        fetchedUser.password.shouldBe(insertedUser.password)
        fetchedUser.vkLink.shouldBe(insertedUser.vkLink)
        fetchedUser.role.shouldBe(insertedUser.role)
    }

    test("User can be fetched by valid login") {
        val fetchedUser =
            userOperations
                .selectUserByLogin(insertedUser.login)
                .shouldNotBeNull()

        fetchedUser.id.shouldBe(insertedUser.id)
        fetchedUser.name.shouldBe(insertedUser.name)
        fetchedUser.surname.shouldBe(insertedUser.surname)
        fetchedUser.login.shouldBe(insertedUser.login)
        fetchedUser.email.shouldBe(insertedUser.email)
        fetchedUser.phoneNumber.shouldBe(insertedUser.phoneNumber)
        fetchedUser.password.shouldBe(insertedUser.password)
        fetchedUser.vkLink.shouldBe(insertedUser.vkLink)
        fetchedUser.role.shouldBe(insertedUser.role)
    }

    test("User can be fetched by valid email") {
        val fetchedUser =
            userOperations
                .selectUserByEmail(insertedUser.email)
                .shouldNotBeNull()

        fetchedUser.id.shouldBe(insertedUser.id)
        fetchedUser.name.shouldBe(insertedUser.name)
        fetchedUser.surname.shouldBe(insertedUser.surname)
        fetchedUser.login.shouldBe(insertedUser.login)
        fetchedUser.email.shouldBe(insertedUser.email)
        fetchedUser.phoneNumber.shouldBe(insertedUser.phoneNumber)
        fetchedUser.password.shouldBe(insertedUser.password)
        fetchedUser.vkLink.shouldBe(insertedUser.vkLink)
        fetchedUser.role.shouldBe(insertedUser.role)
    }

    test("User can be fetched by valid phoneNumber") {
        val fetchedUser =
            userOperations
                .selectUserByPhone(insertedUser.phoneNumber)
                .shouldNotBeNull()

        fetchedUser.id.shouldBe(insertedUser.id)
        fetchedUser.name.shouldBe(insertedUser.name)
        fetchedUser.surname.shouldBe(insertedUser.surname)
        fetchedUser.login.shouldBe(insertedUser.login)
        fetchedUser.email.shouldBe(insertedUser.email)
        fetchedUser.phoneNumber.shouldBe(insertedUser.phoneNumber)
        fetchedUser.password.shouldBe(insertedUser.password)
        fetchedUser.vkLink.shouldBe(insertedUser.vkLink)
        fetchedUser.role.shouldBe(insertedUser.role)
    }

    test("User can be fetched by role") {
        val fetchedUserList =
            userOperations
                .selectUsersByRole(insertedUser.role)
                .shouldNotBeNull()

        fetchedUserList.first().id.shouldBe(insertedUser.id)
        fetchedUserList.first().name.shouldBe(insertedUser.name)
        fetchedUserList.first().surname.shouldBe(insertedUser.surname)
        fetchedUserList.first().login.shouldBe(insertedUser.login)
        fetchedUserList.first().email.shouldBe(insertedUser.email)
        fetchedUserList.first().phoneNumber.shouldBe(insertedUser.phoneNumber)
        fetchedUserList.first().password.shouldBe(insertedUser.password)
        fetchedUserList.first().vkLink.shouldBe(insertedUser.vkLink)
        fetchedUserList.first().role.shouldBe(insertedUser.role)
    }

    test("Two users can be fetched by role") {
        userOperations
            .insertUser(
                validName,
                validUserSurname,
                "1$validLogin",
                "1$validEmail",
                validSecondPhoneNumber,
                appConfiguredPasswordHasher.hash(validPass),
                validVKLink,
                Role.READER,
            ).shouldNotBeNull()

        userOperations
            .selectUsersByRole(insertedUser.role)
            .shouldNotBeNull()
            .shouldHaveSize(2)
    }

    test("One user of two users can be fetched by role") {
        userOperations
            .insertUser(
                validName,
                validUserSurname,
                "1$validLogin",
                "1$validEmail",
                validSecondPhoneNumber,
                appConfiguredPasswordHasher.hash(validPass),
                validVKLink,
                Role.WRITER,
            ).shouldNotBeNull()

        userOperations
            .selectUsersByRole(insertedUser.role)
            .shouldNotBeNull()
            .shouldHaveSize(1)
    }

    listOf(
        "",
        "tenCharact".repeat(11),
        "invalid@gmailcom",
        "invalidgmail.com",
        "invalidgmailcom",
    ).forEach { invalidEmail ->
        test("User can't be fetched by invalid email (this: $invalidEmail)") {
            userOperations.selectUserByEmail(invalidEmail).shouldBeNull()
        }
    }

    test("User can't be fetched by invalid ID") {
        userOperations
            .selectUserByID(Int.MIN_VALUE)
            .shouldBeNull()
    }

    test("User can't be fetched by invalid login") {
        userOperations
            .selectUserByLogin("")
            .shouldBeNull()
    }

    test("User can't be fetched by invalid phone") {
        userOperations
            .selectUserByPhone("")
            .shouldBeNull()
    }

    listOf(Role.ADMIN, Role.MODERATOR, Role.ANONYMOUS).forEach { role ->
        test("Fetches emptyList, when no one user with role == $role exists") {

            // student already added

            userOperations.selectUsersByRole(role)
                .shouldBeEmpty()
        }
    }
})
