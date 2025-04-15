package ru.yarsu.db.users

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

class InsertUserTest : TestcontainerSpec({ context ->
    val userOperations = UserOperations(context)

    test("Valid user can be inserted") {
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
            ).shouldNotBeNull()
    }

    test("Valid user insertion should return this user") {
        val insertedUser =
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

        insertedUser.name.shouldBe(validName)
        insertedUser.surname.shouldBe(validUserSurname)
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe(validEmail)
        insertedUser.phoneNumber.shouldBe(validPhoneNumber.filter { it.isDigit() })
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.vkLink.shouldBe(validVKLink)
        insertedUser.role.shouldBe(Role.READER)
    }

    test("Valid user with long name can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    "a".repeat(User.MAX_NAME_LENGTH),
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber,
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.READER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe("a".repeat(User.MAX_NAME_LENGTH))
        insertedUser.surname.shouldBe(validUserSurname)
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe(validEmail)
        insertedUser.phoneNumber.shouldBe(validPhoneNumber.filter { it.isDigit() })
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.vkLink.shouldBe(validVKLink)
        insertedUser.role.shouldBe(Role.READER)
    }

    test("Valid user with long surname can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    validUserSurname,
                    "a".repeat(User.MAX_SURNAME_LENGTH),
                    validLogin,
                    validEmail,
                    validPhoneNumber,
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.READER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validUserSurname)
        insertedUser.surname.shouldBe("a".repeat(User.MAX_SURNAME_LENGTH))
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe(validEmail)
        insertedUser.phoneNumber.shouldBe(validPhoneNumber.filter { it.isDigit() })
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.vkLink.shouldBe(validVKLink)
        insertedUser.role.shouldBe(Role.READER)
    }

    test("Valid user with long login can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    "a".repeat(User.MAX_LOGIN_LENGTH),
                    validEmail,
                    validPhoneNumber,
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.READER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validName)
        insertedUser.surname.shouldBe(validUserSurname)
        insertedUser.login.shouldBe("a".repeat(User.MAX_LOGIN_LENGTH))
        insertedUser.email.shouldBe(validEmail)
        insertedUser.phoneNumber.shouldBe(validPhoneNumber.filter { it.isDigit() })
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.vkLink.shouldBe(validVKLink)
        insertedUser.role.shouldBe(Role.READER)
    }

    test("Valid user with long email can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    "a".repeat(User.MAX_EMAIL_LENGTH),
                    validPhoneNumber,
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.READER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validName)
        insertedUser.surname.shouldBe(validUserSurname)
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe("a".repeat(User.MAX_EMAIL_LENGTH))
        insertedUser.phoneNumber.shouldBe(validPhoneNumber.filter { it.isDigit() })
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.vkLink.shouldBe(validVKLink)
        insertedUser.role.shouldBe(Role.READER)
    }

    test("Valid user with long phone number can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    "7".repeat(User.MAX_PHONE_NUMBER_LENGTH),
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.READER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validName)
        insertedUser.surname.shouldBe(validUserSurname)
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe(validEmail)
        insertedUser.phoneNumber.shouldBe("7".repeat(User.MAX_PHONE_NUMBER_LENGTH))
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.vkLink.shouldBe(validVKLink)
        insertedUser.role.shouldBe(Role.READER)
    }

    test("Valid user with long password can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber.filter { it.isDigit() },
                    "a".repeat(User.MAX_PASSWORD_LENGTH),
                    validVKLink,
                    Role.READER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validName)
        insertedUser.surname.shouldBe(validUserSurname)
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe(validEmail)
        insertedUser.phoneNumber.shouldBe(validPhoneNumber.filter { it.isDigit() })
        insertedUser.password.shouldBe("a".repeat(User.MAX_PASSWORD_LENGTH))
        insertedUser.vkLink.shouldBe(validVKLink)
        insertedUser.role.shouldBe(Role.READER)
    }

    test("Valid user with long vk link can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber.filter { it.isDigit() },
                    appConfiguredPasswordHasher.hash(validPass),
                    "a".repeat(User.MAX_VK_LINK_LENGTH),
                    Role.READER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validName)
        insertedUser.surname.shouldBe(validUserSurname)
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe(validEmail)
        insertedUser.phoneNumber.shouldBe(validPhoneNumber.filter { it.isDigit() })
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.vkLink.shouldBe("a".repeat(User.MAX_VK_LINK_LENGTH))
        insertedUser.role.shouldBe(Role.READER)
    }

    test("Valid user with role = ADMIN can not be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber.filter { it.isDigit() },
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.ADMIN,
                ).shouldBe(null)
    }

    test("Valid user with role = READER can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber.filter { it.isDigit() },
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.READER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validName)
        insertedUser.surname.shouldBe(validUserSurname)
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe(validEmail)
        insertedUser.phoneNumber.shouldBe(validPhoneNumber.filter { it.isDigit() })
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.vkLink.shouldBe(validVKLink)
        insertedUser.role.shouldBe(Role.READER)
    }

    test("Valid user with role = WRITER can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber.filter { it.isDigit() },
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.WRITER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validName)
        insertedUser.surname.shouldBe(validUserSurname)
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe(validEmail)
        insertedUser.phoneNumber.shouldBe(validPhoneNumber.filter { it.isDigit() })
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.vkLink.shouldBe(validVKLink)
        insertedUser.role.shouldBe(Role.WRITER)
    }

    test("Valid user with role = MODERATOR can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    validName,
                    validUserSurname,
                    validLogin,
                    validEmail,
                    validPhoneNumber.filter { it.isDigit() },
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.MODERATOR,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validName)
        insertedUser.surname.shouldBe(validUserSurname)
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe(validEmail)
        insertedUser.phoneNumber.shouldBe(validPhoneNumber.filter { it.isDigit() })
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.vkLink.shouldBe(validVKLink)
        insertedUser.role.shouldBe(Role.MODERATOR)
    }
})
