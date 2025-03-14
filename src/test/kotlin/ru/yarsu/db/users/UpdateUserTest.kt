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

    lateinit var student: User

    beforeEach {

        student =
            userOperations
                .insertUser(
                    "Student",
                    validEmail,
                    validPass,
                    Role.AUTHORIZED,
                )
                .shouldNotBeNull()
    }

    test("User password can be changed") {
        val validUser =
            userOperations
                .insertUser(
                    validUserName,
                    "teacher$validEmail",
                    validPass,
                    Role.AUTHORIZED,
                ).shouldNotBeNull()

        val newPass = appConfiguredPasswordHasher.hash("newPassword1234 @3243 *%")
        userOperations
            .updatePassword(validUser.id, newPass)
            .shouldNotBeNull().pass shouldBe newPass
    }

    test("Change the role to ANONYMOUS test") {
        userOperations
            .updateRole(student, Role.ANONYMOUS)
            .shouldBeNull()
    }
})
