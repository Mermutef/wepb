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

    val fetchUserByID: (Int) -> Result4k<User, UserFetchingError> = FetchUserByID { userID: Int ->
        usersDatabase.selectUserByID(userID)
    }

    val fetchUserByLogin: (String) -> Result4k<User, UserFetchingError> = FetchUserByLogin { login: String ->
        usersDatabase.selectUserByLogin(login)
    }

    val fetchUserByEmail: (String) -> Result4k<User, UserFetchingError> = FetchUserByEmail { email: String ->
        usersDatabase.selectUserByEmail(email)
    }

    val fetchUserByPhone: (String) -> Result4k<User, UserFetchingError> = FetchUserByPhone { phone: String ->
        usersDatabase.selectUserByPhone(phone)
    }

    val fetchUsersByRole: (Role) -> Result4k<List<User>, UserFetchingError> =
        FetchUsersByRole { userRole: Role -> usersDatabase.selectUsersByRole(userRole) }

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
            insertUser = { name, surname, login, email, phone, pass, vkLink, role ->
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
            fetchUserByLogin = usersDatabase::selectUserByLogin,
            fetchUserByEmail = usersDatabase::selectUserByEmail,
            fetchUserByPhone = usersDatabase::selectUserByPhone,
            config = config
        )

    val changeName: (User, String) -> Result4k<User, FieldChangingError> =
        ChangeStringField(
            maxLength = User.MAX_NAME_LENGTH,
            pattern = User.namePattern,
            changeName = usersDatabase::updateName
        )

    val changeSurname: (User, String) -> Result4k<User, FieldChangingError> =
        ChangeStringField(
            maxLength = User.MAX_SURNAME_LENGTH,
            pattern = User.namePattern,
            changeName = usersDatabase::updateSurname
        )

    val changeEmail: (User, String) -> Result4k<User, FieldChangingError> =
        ChangeStringField(
            maxLength = User.MAX_EMAIL_LENGTH,
            pattern = User.emailPattern,
            changeName = usersDatabase::updateEmail
        )

    val changePhoneNumber: (User, String) -> Result4k<User, FieldChangingError> =
        ChangeStringField(
            maxLength = User.MAX_PHONE_NUMBER_LENGTH,
            pattern = User.phonePattern,
            changeName = usersDatabase::updatePhoneNumber
        )

    val changePassword: (User, String) -> Result4k<User, PasswordChangingError> =
        ChangePassword(
            changePassword = usersDatabase::updatePassword,
            maxLength = User.MAX_PASSWORD_LENGTH,
            config = config,
        )

    val changeVKLink: (User, String) -> Result4k<User, FieldChangingError> =
        ChangeStringField(
            maxLength = User.MAX_VK_LINK_LENGTH,
            pattern = User.vkLinkPattern,
            changeName = usersDatabase::updateVKLink
        )

    val makeReader: (User) -> Result4k<User, MakeRoleError> = RoleChanger(
        targetRole = Role.READER,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_READER,
        usersDatabase::updateRole,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR,
    )

    val makeWriter: (User) -> Result4k<User, MakeRoleError> = RoleChanger(
        targetRole = Role.WRITER,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_WRITER,
        usersDatabase::updateRole,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR,
    )

    val makeModerator: (User) -> Result4k<User, MakeRoleError> = RoleChanger(
        targetRole = Role.MODERATOR,
        alreadyHasRoleError = MakeRoleError.IS_ALREADY_MODERATOR,
        usersDatabase::updateRole,
        unknownError = MakeRoleError.UNKNOWN_DATABASE_ERROR,
    )
}
