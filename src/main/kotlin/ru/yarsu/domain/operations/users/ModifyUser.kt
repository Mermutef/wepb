package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.PasswordHasher
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User

class ChangePassword (
    private val changePassword: (userID: Int, newPassword: String) -> User?,
    private val maxLength: Int,
    config: AppConfig,
) : (User, String) -> Result4k<User, PasswordChangingError> {
    private val hasher = PasswordHasher(config.authConfig)

    override fun invoke(
        user: User,
        newPassword: String,
    ): Result4k<User, PasswordChangingError> =
        try {
            when {
                newPassword.isBlank() ->
                    Failure(PasswordChangingError.PASSWORD_IS_BLANK_OR_EMPTY)
                newPassword.length > maxLength ->
                    Failure(PasswordChangingError.PASSWORD_IS_TOO_LONG)
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
    PASSWORD_IS_TOO_LONG,
}

class ChangeStringField(
    private val maxLength: Int,
    private val pattern: Regex,
    private val changeField: (userID: Int, newName: String) -> User?,
) : (User, String) -> Result4k<User, FieldChangingError> {
    override operator fun invoke(
        user: User,
        newField: String,
    ): Result4k<User, FieldChangingError> =
        try {
            when {
                newField.isBlank() ->
                    Failure(FieldChangingError.FIELD_IS_BLANK_OR_EMPTY)
                newField.length > maxLength ->
                    Failure(FieldChangingError.FIELD_IS_TOO_LONG)
                !pattern.matches(newField) ->
                    Failure(FieldChangingError.FIELD_PATTERN_MISMATCH)
                else -> when (val updatedUser = changeField(user.id, newField)) {
                    is User -> Success(updatedUser)

                    else -> Failure(FieldChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(FieldChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class FieldChangingError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_CHANGING_ERROR,
    FIELD_IS_BLANK_OR_EMPTY,
    FIELD_IS_TOO_LONG,
    FIELD_PATTERN_MISMATCH,
}

class RoleChanger<R : Role, E : Enum<E>>(
    private val targetRole: R,
    private val alreadyHasRoleError: E,
    private val updateRole: (user: User, newRole: Role) -> User?,
    private val unknownError: E,
) : (User) -> Result4k<User, E> {
    override operator fun invoke(user: User): Result4k<User, E> {
        if (user.role == targetRole) {
            return Failure(alreadyHasRoleError)
        }

        return when (val newUser = updateRole(user, targetRole)) {
            is User -> Success(newUser)
            else -> Failure(unknownError)
        }
    }
}

enum class MakeRoleError {
    UNKNOWN_DATABASE_ERROR,
    IS_ALREADY_READER,
    IS_ALREADY_WRITER,
    IS_ALREADY_MODERATOR,
}
