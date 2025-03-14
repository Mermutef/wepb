package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.PasswordHasher
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.models.UserValidationResult

class CreateUser (
    private val insertUser: (name: String, email: String, pass: String, role: Role) -> User?,
    private val fetchUserByName: (String) -> User?,
    private val fetchUserByEmail: (String) -> User?,
    config: AppConfig,
) : (String, String, String, Role) -> Result4k<User, UserCreationError> {
    private val hasher = PasswordHasher(config.authConfig)

    override operator fun invoke(
        name: String,
        email: String,
        pass: String,
        role: Role,
    ): Result4k<User, UserCreationError> =
        when {
            User.validateUserData(name, email, pass) != UserValidationResult.ALL_OK ->
                Failure(UserCreationError.INVALID_USER_DATA)
            nameAlreadyExists(name) ->
                Failure(UserCreationError.NAME_ALREADY_EXISTS)
            emailAlreadyExists(email) ->
                Failure(UserCreationError.EMAIL_ALREADY_EXISTS)
            else ->
                when (
                    val newUser =
                        insertUser(
                            name,
                            email,
                            hasher.hash(pass),
                            role,
                        )
                ) {
                    is User -> Success(newUser)
                    else -> Failure(UserCreationError.UNKNOWN_DATABASE_ERROR)
                }
        }

    private fun nameAlreadyExists(name: String): Boolean =
        when (fetchUserByName(name)) {
            is User -> true
            else -> false
        }

    private fun emailAlreadyExists(email: String): Boolean =
        when (fetchUserByEmail(email)) {
            is User -> true
            else -> false
        }
}

enum class UserCreationError {
    NAME_ALREADY_EXISTS,
    EMAIL_ALREADY_EXISTS,
    INVALID_USER_DATA,
    UNKNOWN_DATABASE_ERROR,
}
