package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.Result4k
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.dependencies.UsersDatabase
import ru.yarsu.domain.models.User

class UserOperationsHolder (
    private val usersDatabase: UsersDatabase,
    config: AppConfig,
) {
    val fetchAllUsers: () -> Result4k<List<User>, UserFetchingError> = FetchAllUsers { usersDatabase.selectAllUsers() }

    val fetchUsersByRole: (Role) -> Result4k<List<User>, UserFetchingError> =
        FetchUsersByRole { userRole: Role -> usersDatabase.selectUsersByRole(userRole) }

    val fetchUserByID: (Int) -> Result4k<User, UserFetchingError> = FetchUserByID { userID: Int ->
        usersDatabase.selectUserByID(userID)
    }

    val fetchUserByEmail: (String) -> Result4k<User, UserFetchingError> = FetchUserByEmail { email: String ->
        usersDatabase.selectUserByEmail(email)
    }

    val fetchUserByName: (String) -> Result4k<User, UserFetchingError> = FetchUserByName { userName: String ->
        usersDatabase.selectUserByName(userName)
    }

    val createUser: (name: String, email: String, password: String, role: Role) -> Result4k<User, UserCreationError> =
        CreateUser(
            insertUser = { name: String, email: String, pass: String, role: Role ->
                usersDatabase.insertUser(name, email, pass, role)
            },
            fetchUserByName = { name: String ->
                usersDatabase.selectUserByName(name)
            },
            fetchUserByEmail = { email: String ->
                usersDatabase.selectUserByEmail(email)
            },
            config = config,
        )

//    val changePassword: (User, String) -> Result4k<User, PasswordChangingError> =
//        ChangePassword(
//            changePassword = usersDatabase::updatePassword,
//            config = config,
//        )
}
