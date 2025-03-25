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
                    "reader$validEmail",
                    validPhoneNumber,
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.READER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validName)
        insertedUser.login.shouldBe(validLogin)
        insertedUser.email.shouldBe("reader$validEmail")
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.role.shouldBe(Role.READER)
    }

    test("Valid user with long name can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    "a".repeat(User.MAX_LOGIN_LENGTH),
                    validUserSurname,
                    validLogin,
                    "reader$validEmail",
                    validPhoneNumber,
                    appConfiguredPasswordHasher.hash(validPass),
                    validVKLink,
                    Role.READER,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe("a".repeat(User.MAX_LOGIN_LENGTH))
        insertedUser.email.shouldBe("reader$validEmail")
        insertedUser.password.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.role.shouldBe(Role.READER)
    }
})
