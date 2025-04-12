package ru.yarsu.db.users

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

class UpdateUserTest : TestcontainerSpec({ context ->
    val userOperations = UserOperations(context)

    lateinit var reader: User

    beforeEach {
        reader =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber,
                    validPass,
                    validVKLink,
                    Role.READER,
                )
                .shouldNotBeNull()
    }

    test("User password can be changed") {
        val newPass = appConfiguredPasswordHasher.hash("newPassword1234 @3243 *%")
        userOperations
            .updatePassword(reader.id, newPass)
            .shouldNotBeNull().password shouldBe newPass
    }

    test("Change the role to ANONYMOUS") {
        userOperations
            .updateRole(reader, Role.ANONYMOUS)
            .shouldBeNull()
    }

    test("Reader role can be changed to Writer") {
        val validReader =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    "1$validLogin",
                    "1$validEmail",
                    validSecondPhoneNumber,
                    validPass,
                    validVKLink,
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
                    validName,
                    validUserSurname,
                    "1$validLogin",
                    "1$validEmail",
                    validSecondPhoneNumber,
                    validPass,
                    validVKLink,
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
                    validName,
                    validUserSurname,
                    "1$validLogin",
                    "1$validEmail",
                    validSecondPhoneNumber,
                    validPass,
                    validVKLink,
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
                    validName,
                    validUserSurname,
                    "1$validLogin",
                    "1$validEmail",
                    validSecondPhoneNumber,
                    validPass,
                    validVKLink,
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
                    validName,
                    validUserSurname,
                    "1$validLogin",
                    "1$validEmail",
                    validSecondPhoneNumber,
                    validPass,
                    validVKLink,
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
                    validName,
                    validUserSurname,
                    "1$validLogin",
                    "1$validEmail",
                    validSecondPhoneNumber,
                    validPass,
                    validVKLink,
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
