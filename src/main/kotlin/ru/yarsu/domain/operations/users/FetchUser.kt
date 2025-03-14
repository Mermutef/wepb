package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User

class FetchUserByID (
    private val fetchUserByID: (Int) -> User?,
) : (Int) -> Result4k<User, UserFetchingError> {

    override operator fun invoke(userId: Int): Result4k<User, UserFetchingError> =
        try {
            when (val user = fetchUserByID(userId)) {
                is User -> Success(user)
                else -> Failure(UserFetchingError.NO_SUCH_USER)
            }
        } catch (_: DataAccessException) {
            Failure(UserFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchUserByName (
    private val fetchUserByName: (String) -> User?,
) : (String) -> Result4k<User, UserFetchingError> {

    override operator fun invoke(userName: String): Result4k<User, UserFetchingError> =
        try {
            fetchUserByName(userName)
                .let { user ->
                    when (user) {
                        is User -> Success(user)
                        else -> Failure(UserFetchingError.NO_SUCH_USER)
                    }
                }
        } catch (_: DataAccessException) {
            Failure(UserFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchUserByEmail (
    private val fetchUserByEmail: (String) -> User?,
) : (String) -> Result4k<User, UserFetchingError> {

    override fun invoke(email: String): Result4k<User, UserFetchingError> =
        try {
            fetchUserByEmail(email)
                .let { user ->
                    when (user) {
                        is User -> Success(user)
                        else -> Failure(UserFetchingError.NO_SUCH_USER)
                    }
                }
        } catch (_: DataAccessException) {
            Failure(UserFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchUsersByRole (
    private val fetchUsersByRole: (Role) -> List<User>,
) : (Role) -> Result4k<List<User>, UserFetchingError> {

    override operator fun invoke(role: Role): Result4k<List<User>, UserFetchingError> =
        try {
            Success(fetchUsersByRole(role))
        } catch (_: DataAccessException) {
            Failure(UserFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchAllUsers (
    private val fetchAllUsers: () -> List<User>,
) : () -> Result4k<List<User>, UserFetchingError> {

    override operator fun invoke(): Result4k<List<User>, UserFetchingError> =
        try {
            Success(fetchAllUsers())
        } catch (_: DataAccessException) {
            Failure(UserFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class UserFetchingError {
    UNKNOWN_DATABASE_ERROR,
    NO_SUCH_USER,
}
