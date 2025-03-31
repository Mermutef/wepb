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
                    Role.READER,
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
                    Role.READER,
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

    /*
    val userOperations = UserOperations(context)

    lateinit var student: User

    beforeEach {

        student =
            userOperations
                .insertUser(
                    "Student",
                    validEmail,
                    validPass,
                    studentRole,
                )
                .shouldNotBeNull()
    }

    test("Student role can be changed to teacher") {
        val validStudent =
            userOperations
                .insertUser(
                    validUserName,
                    "student$validEmail",
                    validPass,
                    studentRole,
                ).shouldNotBeNull()

        userOperations
            .makeTeacher(
                validStudent,
            ).shouldNotBeNull()
            .role
            .shouldBe(Role.TEACHER)
    }

    test("Teacher role can be changed to student") {
        val validTeacher =
            userOperations
                .insertUser(
                    validUserName,
                    "teacher$validEmail",
                    validPass,
                    teacherRole,
                ).shouldNotBeNull()

        userOperations
            .makeStudent(
                validTeacher,
            ).shouldNotBeNull()
            .role
            .shouldBe(Role.STUDENT)
    }

    test("User password can be changed") {
        val validUser =
            userOperations
                .insertUser(
                    validUserName,
                    "teacher$validEmail",
                    validPass,
                    teacherRole,
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
     */
})
