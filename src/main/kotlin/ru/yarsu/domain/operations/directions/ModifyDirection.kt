//package ru.yarsu.domain.operations.users
//
//import dev.forkhandles.result4k.Failure
//import dev.forkhandles.result4k.Result4k
//import dev.forkhandles.result4k.Success
//import org.jooq.exception.DataAccessException
//import ru.yarsu.config.AppConfig
//import ru.yarsu.domain.accounts.PasswordHasher
//import ru.yarsu.domain.accounts.Role
//import ru.yarsu.domain.models.User
//git
//class UpdateDirection(
//    private val targetRole: R,
//    private val alreadyHasRoleError: E,
//    private val updateRole: (user: User, newRole: Role) -> User?,
//    private val unknownError: E,
//) : (User) -> Result4k<User, E> {
//    override operator fun invoke(user: User): Result4k<User, E> {
//        if (user.role == targetRole) {
//            return Failure(alreadyHasRoleError)
//        }
//
//        return when (val newUser = updateRole(user, targetRole)) {
//            is User -> Success(newUser)
//            else -> Failure(unknownError)
//        }
//    }
//}
//
//enum class MakeRolError {
//    UNKNOWN_DATABASE_ERROR,
//    IS_ALREADY_READER,
//    IS_ALREADY_WRITER,
//    IS_ALREADY_MODERATOR,
//}
