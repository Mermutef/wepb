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
                    "reader1$validEmail",
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
                validPhoneNumber,
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

    test("User can be fetched by valid login") {
        val fetchedUser =
            userOperations
                .selectUserByLogin(insertedUser.login)
                .shouldNotBeNull()

        fetchedUser.name.shouldBe(validName)
        fetchedUser.email.shouldBe("reader1$validEmail")
        fetchedUser.password
            .shouldBe(appConfiguredPasswordHasher.hash(validPass))
        fetchedUser.role.shouldBe(Role.READER)
        fetchedUser.id.shouldBe(insertedUser.id)
    }

    test("User can't be fetched by invalid name") {
        userOperations
            .selectUserByLogin("")
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
