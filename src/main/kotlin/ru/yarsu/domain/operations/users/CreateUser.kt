package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.PasswordHasher
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User

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
        phoneNumber: String,
        email: String,
        pass: String,
        vkLink: String?,
        role: Role,
    ): Result4k<User, UserCreationError> =
        when {
            !isValidUserData(name, surname, login, phoneNumber, email, pass) ->
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
                        phoneNumber.filter { it.isDigit() }, // + проверка, что начинается с 79 и длиной 11 символов
                        email,
                        hasher.hash(pass),
                        vkLink,
                        role
                    )
                ) {
                    is User -> Success(newUser)
                    else -> Failure(UserCreationError.UNKNOWN_DATABASE_ERROR)
                }
        }

    private fun isValidUserData(
        name: String,
        surname: String,
        login: String,
        phoneNumber: String,
        email: String,
        pass: String,
    ): Boolean {
        // Здесь можно добавить более сложную логику валидации
        return name.isNotBlank() &&
            surname.isNotBlank() &&
            login.isNotBlank() &&
            isValidPhone(phoneNumber) &&
            isValidEmail(email) &&
            isStrongPassword(pass)
    }

    private fun isValidPhone(phone: String): Boolean {
        // Простая проверка номера телефона
        return phone.matches(Regex("^\\+?[0-9\\-() ]+\$"))
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(User.emailPattern)
    }

    private fun isStrongPassword(pass: String): Boolean {
        return pass.length >= 8
    }

    private fun loginAlreadyExists(login: String): Boolean = fetchUserByLogin(login) != null

    private fun emailAlreadyExists(email: String): Boolean = fetchUserByEmail(email) != null

    private fun phoneAlreadyExists(phone: String): Boolean = fetchUserByPhone(phone) != null
}

enum class UserCreationError {
    LOGIN_ALREADY_EXISTS,
    EMAIL_ALREADY_EXISTS,
    PHONE_ALREADY_EXISTS,
    INVALID_USER_DATA,
    UNKNOWN_DATABASE_ERROR,
}
