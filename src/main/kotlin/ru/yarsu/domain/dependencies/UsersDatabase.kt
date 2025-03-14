package ru.yarsu.domain.dependencies

import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User

interface UsersDatabase {

    fun selectUserByID(userID: Int): User?

    fun selectUserByName(userName: String): User?

    fun selectUserByEmail(email: String): User?

    fun selectUsersByRole(userRole: Role): List<User>

    fun selectAllUsers(): List<User>

    fun insertUser(
        name: String,
        email: String,
        pass: String,
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
