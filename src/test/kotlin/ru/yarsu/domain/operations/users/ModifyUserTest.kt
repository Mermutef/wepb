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
    val validUser = User(
        1,
        validUserName,
        validEmail,
        validPass,
        Role.AUTHORIZED
    )

    val changePasswordMock: (userID: Int, newPassword: String) -> User? =
        { _, newPass -> validUser.copy(pass = newPass) }

    val changePassword = ChangePassword(changePasswordMock, config)

    test("Password cannot be changed to empty password") {
        changePassword(validUser, "").shouldBeFailure(PasswordChangingError.PASSWORD_IS_BLANK_OR_EMPTY)
    }

    test("Password cannot be changed to blank password") {
        changePassword(validUser, "  \t\n")
            .shouldBeFailure(PasswordChangingError.PASSWORD_IS_BLANK_OR_EMPTY)
    }
})
