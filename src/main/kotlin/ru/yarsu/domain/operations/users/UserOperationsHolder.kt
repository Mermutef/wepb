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

    val fetchUserByName: (String) -> Result4k<User, UserFetchingError> = FetchUserByLogin { userName: String ->
        usersDatabase.selectUserByLogin(userName)
    }

    val createUser: (
        name: String,
        surname: String,
        login: String,
        email: String,
        phoneNumber: String,
        password: String,
        vkLink: String?,
        role: Role,
    ) -> Result4k<User, UserCreationError> =
        CreateUser(
            insertUser = { name, surname, login, phone, email, pass, vkLink, role ->
                usersDatabase.insertUser(
                    name = name,
                    surname = surname,
                    login = login,
                    email = email,
                    phoneNumber = phone,
                    password = pass,
                    vkLink = vkLink,
                    role = role
                )
            },
            fetchUserByLogin = { login ->
                usersDatabase.selectUserByLogin(login)
            },
            fetchUserByEmail = usersDatabase::selectUserByEmail, // вот так же сделать
            fetchUserByPhone = usersDatabase::selectUserByPhone,
            config = config
        )

    val changePassword: (User, String) -> Result4k<User, PasswordChangingError> =
        ChangePassword(
            changePassword = usersDatabase::updatePassword,
            config = config,
        )

    val makeReader: (User) -> Result4k<User, MakeRoleError> = RoleChanger(
        targetRole = Role.READER,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_READER,
        usersDatabase::updateRole,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR,
    )

    val makeWriter = RoleChanger(
        targetRole = Role.WRITER,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_WRITER,
        usersDatabase::updateRole,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR,
    )

    val makeModerator = RoleChanger(
        targetRole = Role.MODERATOR,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_MODERATOR,
        usersDatabase::updateRole,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR,
    )
}
