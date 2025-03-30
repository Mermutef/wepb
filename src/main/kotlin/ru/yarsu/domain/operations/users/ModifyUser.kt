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

class MakeReader (
    private val updateRole: (user: User) -> User?,
) : (User) -> Result4k<User, MakeReaderError> {
    override operator fun invoke(user: User): Result4k<User, MakeReaderError> =
        if (user.role == Role.READER) {
            Failure(MakeReaderError.IS_ALREADY_READER)
        } else {
            when (
                val newUser =
                    updateRole(user, Role.READER)
            ) {
                is User -> Success(newUser)
                else -> Failure(MakeReaderError.UNKNOWN_DATABASE_ERROR)
            }
        }
}

enum class MakeReaderError {
    UNKNOWN_DATABASE_ERROR,
    IS_ALREADY_READER,
}

class MakeWriter (
    private val updateRole: (user: User) -> User?,
) : (User) -> Result4k<User, MakeWriterError> {
    override operator fun invoke(user: User): Result4k<User, MakeWriterError> =
        if (user.role == Role.WRITER) {
            Failure(MakeWriterError.IS_ALREADY_WRITER)
        } else {
            when (
                val newUser =
                    updateRole(user, Role.WRITER)
            ) {
                is User -> Success(newUser)
                else -> Failure(MakeWriterError.UNKNOWN_DATABASE_ERROR)
            }
        }
}

enum class MakeWriterError {
    UNKNOWN_DATABASE_ERROR,
    IS_ALREADY_WRITER,
}

class MakeModerator (
    private val updateRole: (user: User) -> User?,
) : (User) -> Result4k<User, MakeModeratorError> {
    override operator fun invoke(user: User): Result4k<User, MakeModeratorError> =
        if (user.role == Role.MODERATOR) {
            Failure(MakeModeratorError.IS_ALREADY_MODERATOR)
        } else {
            when (
                val newUser =
                    updateRole(user, Role.MODERATOR)
            ) {
                is User -> Success(newUser)
                else -> Failure(MakeModeratorError.UNKNOWN_DATABASE_ERROR)
            }
        }
}

enum class MakeModeratorError {
    UNKNOWN_DATABASE_ERROR,
    IS_ALREADY_MODERATOR,
}

