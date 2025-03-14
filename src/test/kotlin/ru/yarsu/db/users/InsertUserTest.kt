package ru.yarsu.db.users

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.appConfiguredPasswordHasher
import ru.yarsu.db.validEmail
import ru.yarsu.db.validPass
import ru.yarsu.db.validUserName
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User

class InsertUserTest : TestcontainerSpec({ context ->
    val userOperations = UserOperations(context)

    test("Valid user can be inserted") {
        userOperations
            .insertUser(
                validUserName,
                validEmail,
                validPass,
                Role.AUTHORIZED,
            ).shouldNotBeNull()
    }

    test("Valid user insertion should return this user") {
        val insertedUser =
            userOperations
                .insertUser(
                    validUserName,
                    "student$validEmail",
                    appConfiguredPasswordHasher.hash(validPass),
                    Role.AUTHORIZED,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe(validUserName)
        insertedUser.email.shouldBe("student$validEmail")
        insertedUser.pass.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.role.shouldBe(Role.AUTHORIZED)
    }

    test("Valid user with long name can be inserted") {
        val insertedUser =
            userOperations
                .insertUser(
                    "a".repeat(User.MAX_NAME_LENGTH),
                    "student$validEmail",
                    appConfiguredPasswordHasher.hash(validPass),
                    Role.AUTHORIZED,
                ).shouldNotBeNull()

        insertedUser.name.shouldBe("a".repeat(User.MAX_NAME_LENGTH))
        insertedUser.email.shouldBe("student$validEmail")
        insertedUser.pass.shouldBe(appConfiguredPasswordHasher.hash(validPass))
        insertedUser.role.shouldBe(Role.AUTHORIZED)
    }
})
