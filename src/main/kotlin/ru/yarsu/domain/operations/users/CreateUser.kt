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
    private val insertUser: (
        name: String,
        surname: String,
        login: String,
        email: String,
        phoneNumber: String,
        password: String,
        vkLink: String?,
        role: Role,
    ) -> User?,
    private val fetchUserByLogin: (String) -> User?,
    private val fetchUserByEmail: (String) -> User?,
    private val fetchUserByPhone: (String) -> User?,
    config: AppConfig,
) : (String, String, String, String, String, String, String?, Role) -> Result4k<User, UserCreationError> {

    private val hasher = PasswordHasher(config.authConfig)

    override operator fun invoke(
        name: String,
        surname: String,
        login: String,
        email: String,
        phoneNumber: String,
        pass: String,
        vkLink: String?,
        role: Role,
    ): Result4k<User, UserCreationError> =
        when {
            User.validateUserData(name, surname, login, email, phoneNumber.filter { it.isDigit() }, pass, vkLink)
                != UserValidationResult.ALL_OK ->
                Failure(UserCreationError.INVALID_USER_DATA)
            loginAlreadyExists(login) ->
                Failure(UserCreationError.LOGIN_ALREADY_EXISTS)
            emailAlreadyExists(email) ->
                Failure(UserCreationError.EMAIL_ALREADY_EXISTS)
            phoneAlreadyExists(phoneNumber) ->
                Failure(UserCreationError.PHONE_ALREADY_EXISTS)
            else ->
                when (
                    val newUser = insertUser(
                        name,
                        surname,
                        login,
                        email,
                        phoneNumber.filter { it.isDigit() }, // + проверка, что начинается с 79 и длиной 11 символов
                        hasher.hash(pass),
                        vkLink,
                        role
                    )
                ) {
                    is User -> Success(newUser)
                    else -> Failure(UserCreationError.UNKNOWN_DATABASE_ERROR)
                }
        }

    private fun loginAlreadyExists(login: String): Boolean =
        when (fetchUserByLogin(login)) {
            is User -> true
            else -> false
        }

    private fun emailAlreadyExists(email: String): Boolean =
        when (fetchUserByEmail(email)) {
            is User -> true
            else -> false
        }

    private fun phoneAlreadyExists(phone: String): Boolean =
        when (fetchUserByPhone(phone)) {
            is User -> true
            else -> false
        }
}

enum class UserCreationError {
    LOGIN_ALREADY_EXISTS,
    EMAIL_ALREADY_EXISTS,
    PHONE_ALREADY_EXISTS,
    INVALID_USER_DATA,
    UNKNOWN_DATABASE_ERROR,
}
