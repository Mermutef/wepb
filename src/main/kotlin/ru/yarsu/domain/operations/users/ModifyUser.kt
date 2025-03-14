package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.PasswordHasher
import ru.yarsu.domain.models.User

class ChangePassword (
    private val changePassword: (userID: Int, newPassword: String) -> User?,
    config: AppConfig,
) : (User, String) -> Result4k<User, PasswordChangingError> {
    private val hasher = PasswordHasher(config.authConfig)

    override fun invoke(
        user: User,
        newPassword: String,
    ): Result4k<User, PasswordChangingError> =
        try {
            when {
                newPassword.isBlank() || newPassword.isEmpty() ->
                    Failure(PasswordChangingError.PASSWORD_IS_BLANK_OR_EMPTY)
                else -> when (val userWithNewPassword = changePassword(user.id, hasher.hash(newPassword))) {
                    is User -> Success(userWithNewPassword)

                    else -> Failure(PasswordChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(PasswordChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class PasswordChangingError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_CHANGING_ERROR,
    PASSWORD_IS_BLANK_OR_EMPTY,
}
