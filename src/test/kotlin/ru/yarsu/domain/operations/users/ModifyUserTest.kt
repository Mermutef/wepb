package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.kotest.shouldBeFailure
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.config
import ru.yarsu.domain.operations.validEmail
import ru.yarsu.domain.operations.validPass
import ru.yarsu.domain.operations.validUserName

class ModifyUserTest : FunSpec({
    val validAnonymous = User(
        1,
        validUserName,
        validEmail,
        validPass,
        Role.ANONYMOUS
    )

    val validReader = User(
        1,
        validUserName,
        validEmail,
        validPass,
        Role.READER
    )

    val validWriter = User(
        1,
        validUserName,
        validEmail,
        validPass,
        Role.WRITER,
        )

    val validModerator = User(
        1,
        validUserName,
        validEmail,
        validPass,
        Role.MODERATOR,
    )

    val changePasswordMock: (userID: Int, newPassword: String) -> User? =
        { _, newPass -> validAnonymous.copy(pass = newPass) }

    val changePassword = ChangePassword(changePasswordMock, config)

    test("Password cannot be changed to empty password") {
        changePassword(validAnonymous, "").shouldBeFailure(PasswordChangingError.PASSWORD_IS_BLANK_OR_EMPTY)
    }

    test("Password cannot be changed to blank password") {
        changePassword(validAnonymous, "  \t\n")
            .shouldBeFailure(PasswordChangingError.PASSWORD_IS_BLANK_OR_EMPTY)
    }

    val makeReaderMock: (user: User) -> User? = { user ->
        User(
            user.id,
            user.name,
            user.email,
            user.pass,
            Role.READER,
        )
    }

    val makeReaderNullMock: (user: User) -> User? = { _ -> null }

    val makeWriterMock: (user: User) -> User? = { user ->
        User(
            user.id,
            user.name,
            user.email,
            user.pass,
            Role.WRITER,
        )
    }
    val makeWriterNullMock: (user: User) -> User? = { _ -> null }

    val makeModeratorMock: (user: User) -> User? = { user ->
        User(
            user.id,
            user.name,
            user.email,
            user.pass,
            Role.MODERATOR,
        )
    }
    val makeModeratorNullMock: (user: User) -> User? = { _ -> null }
    val oper = UserOperationsHolder()
    val makeTeacher = (makeTeacherMock)
    val makeStudent = MakeStudent(makeStudentMock)
    val makeTeacherNull = MakeTeacher(makeTeacherNullMock)
    val makeStudentNull = MakeStudent(makeStudentNullMock)

    test("Student role can be changed to teacher") {
        makeTeacher(validStudent)
            .shouldBeSuccess()
    }

    test("Student role cannot be changed to student") {
        makeStudent(validStudent) shouldBeFailure
                MakeStudentError.IS_ALREADY_STUDENT
    }

    test("Unknown db error test for MakeStudent") {
        makeStudentNull(validTeacher) shouldBeFailure
                MakeStudentError.UNKNOWN_DATABASE_ERROR
    }

    test("Teacher role can be changed to student") {
        makeStudent(validTeacher)
            .shouldBeSuccess().role shouldBe Role.TEACHER
    }

    test("Teacher role cannot be changed to teacher") {
        makeTeacher(validTeacher) shouldBeFailure
                MakeTeacherError.IS_ALREADY_TEACHER
    }

    test("Unknown db error test for MakeTeacher") {
        makeTeacherNull(validStudent) shouldBeFailure
                MakeTeacherError.UNKNOWN_DATABASE_ERROR
    }

})
