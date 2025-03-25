package ru.yarsu.domain.dependencies

import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User

interface UsersDatabase {
    fun selectUserByID(userID: Int): User?

    fun selectUserByLogin(login: String): User?

    fun selectUserByEmail(email: String): User?

    fun selectUserByPhone(phoneNumber: String): User?

    fun selectUsersByRole(userRole: Role): List<User>

    fun selectAllUsers(): List<User>

    @Suppress("detekt:LongParameterList")
    fun insertUser(
        name: String,
        surname: String,
        login: String,
        email: String,
        phoneNumber: String,
        password: String,
        vkLink: String?,
        role: Role,
    ): User?

    fun updateRole(
        user: User,
        newRole: Role,
    ): User?

    fun updatePassword(
        userID: Int,
        newPassword: String,
    ): User?
}
