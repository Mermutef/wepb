package ru.yarsu.db.users

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

class UpdateUserTest : TestcontainerSpec({ context ->
    val userOperations = UserOperations(context)

    lateinit var reader: User

    beforeEach {
        reader =
            userOperations
                .insertUser(
                    "ReaderUpdate1",
                    "readerUpdate1$validEmail",
                    validPass,
                    Role.READER,
                )
                .shouldNotBeNull()
    }

    test("User password can be changed") {
        val validUser =
            userOperations
                .insertUser(
                    validUserName,
                    "readerUpdate2$validEmail",
                    validPass,
                    Role.READER,
                ).shouldNotBeNull()

        val newPass = appConfiguredPasswordHasher.hash("newPassword1234 @3243 *%")
        userOperations
            .updatePassword(validUser.id, newPass)
            .shouldNotBeNull().pass shouldBe newPass
    }

    test("Change the role to ANONYMOUS test") {
        userOperations
            .updateRole(reader, Role.ANONYMOUS)
            .shouldBeNull()
    }

    beforeEach {
        reader =
            userOperations
                .insertUser(
                    "Reader",
                    validEmail,
                    validPass,
                    Role.READER,
                )
                .shouldNotBeNull()
    }

    test("Reader role can be changed to Writer") {
        val validReader =
            userOperations
                .insertUser(
                    validUserName,
                    "reader$validEmail",
                    validPass,
                    Role.READER,
                ).shouldNotBeNull()

        userOperations
            .updateRole(
                validReader,
                Role.WRITER
            ).shouldNotBeNull()
            .role
            .shouldBe(Role.WRITER)
    }

    test("Reader role can be changed to Moderator") {
        val validReader =
            userOperations
                .insertUser(
                    validUserName,
                    "reader$validEmail",
                    validPass,
                    Role.READER,
                ).shouldNotBeNull()

        userOperations
            .updateRole(
                validReader,
                Role.MODERATOR
            ).shouldNotBeNull()
            .role
            .shouldBe(Role.MODERATOR)
    }

    test("Writer role can be changed to Reader") {
        val validWriter =
            userOperations
                .insertUser(
                    validUserName,
                    "writer$validEmail",
                    validPass,
                    Role.WRITER,
                ).shouldNotBeNull()

        userOperations
            .updateRole(
                validWriter,
                Role.READER
            ).shouldNotBeNull()
            .role
            .shouldBe(Role.READER)
    }

    test("Writer role can be changed to Moderator") {
        val validWriter =
            userOperations
                .insertUser(
                    validUserName,
                    "writer$validEmail",
                    validPass,
                    Role.WRITER,
                ).shouldNotBeNull()

        userOperations
            .updateRole(
                validWriter,
                Role.MODERATOR
            ).shouldNotBeNull()
            .role
            .shouldBe(Role.MODERATOR)
    }

    test("Moderator role can be changed to Reader") {
        val validModerator =
            userOperations
                .insertUser(
                    validUserName,
                    "moder$validEmail",
                    validPass,
                    Role.MODERATOR,
                ).shouldNotBeNull()

        userOperations
            .updateRole(
                validModerator,
                Role.READER
            ).shouldNotBeNull()
            .role
            .shouldBe(Role.READER)
    }

    test("Moderator role can be changed to Writer") {
        val validModerator =
            userOperations
                .insertUser(
                    validUserName,
                    "moder$validEmail",
                    validPass,
                    Role.MODERATOR,
                ).shouldNotBeNull()

        userOperations
            .updateRole(
                validModerator,
                Role.WRITER
            ).shouldNotBeNull()
            .role
            .shouldBe(Role.WRITER)
    }
})
