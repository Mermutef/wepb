package ru.yarsu.db.users

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.appConfiguredPasswordHasher
import ru.yarsu.db.validEmail
import ru.yarsu.db.validPass
import ru.yarsu.db.validUserName
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.adminRole

class SelectUserTest : TestcontainerSpec({ context ->
    val userOperations = UserOperations(context)
    lateinit var insertedUser: User

    beforeEach {
        insertedUser =
            userOperations
                .insertUser(
                    validUserName,
                    "student1$validEmail",
                    appConfiguredPasswordHasher.hash(validPass),
                    Role.AUTHORIZED,
                ).shouldNotBeNull()
    }

    test("There is only general admin user by default") {
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
                "student2$validEmail",
                "pass",
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

        fetchedUser.name.shouldBe(validUserName)
        fetchedUser.email.shouldBe("student1$validEmail")
        fetchedUser.pass
            .shouldBe(appConfiguredPasswordHasher.hash(validPass))
        fetchedUser.role.shouldBe(Role.AUTHORIZED)
        fetchedUser.id.shouldBe(insertedUser.id)
    }

    test("User can be fetched by valid email") {
        val fetchedUser =
            userOperations
                .selectUserByEmail(insertedUser.email)
                .shouldNotBeNull()

        fetchedUser.name.shouldBe(insertedUser.name)
        fetchedUser.email.shouldBe(insertedUser.email)
        fetchedUser.pass
            .shouldBe(insertedUser.pass)
        fetchedUser.role.shouldBe(insertedUser.role)
        fetchedUser.id.shouldBe(insertedUser.id)
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

    test("User can be fetched by valid name") {
        val fetchedUser =
            userOperations
                .selectUserByName(insertedUser.name)
                .shouldNotBeNull()

        fetchedUser.name.shouldBe(validUserName)
        fetchedUser.email.shouldBe("student1$validEmail")
        fetchedUser.pass
            .shouldBe(appConfiguredPasswordHasher.hash(validPass))
        fetchedUser.role.shouldBe(Role.AUTHORIZED)
        fetchedUser.id.shouldBe(insertedUser.id)
    }

    test("User can't be fetched by invalid name") {
        userOperations
            .selectUserByName("")
            .shouldBeNull()
    }

    Role.entries.minus(Role.ANONYMOUS).forEach { role ->
        test("Users can be fetched by role == $role") {

            // user already added

            userOperations
                .insertUser(
                    "Teacher",
                    validEmail,
                    "pass",
                    Role.MODERATOR,
                )

            userOperations
                .insertUser(
                    "Admin",
                    "admin$validEmail",
                    "pass",
                    adminRole,
                )

            val fetchedUsers =
                userOperations.selectUsersByRole(role)
                    .shouldNotBeEmpty()
                    .shouldHaveSize(1)

            fetchedUsers.first().role.shouldBe(role)
        }
    }

    listOf(Role.ADMIN, Role.MODERATOR, Role.ANONYMOUS).forEach { role ->
        test("Fetches emptyList, when no one user with role == $role exists") {

            // student already added

            userOperations.selectUsersByRole(role)
                .shouldBeEmpty()
        }
    }
})
