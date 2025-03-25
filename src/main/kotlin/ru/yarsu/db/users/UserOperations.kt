package ru.yarsu.db.users

import org.jooq.DSLContext
import org.jooq.Record
import ru.yarsu.db.generated.enums.UserRole
import ru.yarsu.db.generated.tables.Users.Companion.USERS
import ru.yarsu.db.utils.safeLet
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.dependencies.UsersDatabase
import ru.yarsu.domain.models.User

@Suppress("detekt:TooManyFunctions")
class UserOperations(
    private val jooqContext: DSLContext,
) : UsersDatabase {
    override fun selectUserByID(userID: Int): User? =
        selectFromUsers()
            .where(USERS.ID.eq(userID))
            .fetchOne()
            ?.toUser()

    override fun selectUserByLogin(login: String): User? =
        selectFromUsers()
            .where(USERS.LOGIN.eq(login))
            .fetchOne()
            ?.toUser()

    override fun selectUserByEmail(email: String): User? =
        selectFromUsers()
            .where(USERS.EMAIL.eq(email))
            .fetchOne()
            ?.toUser()

    override fun selectUserByPhone(phoneNumber: String): User? =
        selectFromUsers()
            .where(USERS.PHONENUMBER.eq(phoneNumber))
            .fetchOne()
            ?.toUser()

    override fun selectUsersByRole(userRole: Role): List<User> =
        selectFromUsers()
            .where(USERS.ROLE.eq(userRole.asDbRole()))
            .fetch()
            .mapNotNull { it.toUser() }

    override fun selectAllUsers(): List<User> =
        selectFromUsers()
            .fetch()
            .mapNotNull { it.toUser() }

    override fun insertUser(
        name: String,
        surname: String,
        login: String,
        email: String,
        phoneNumber: String,
        password: String,
        vkLink: String?,
        role: Role,
    ): User? =
        jooqContext.insertInto(USERS)
            .set(USERS.NAME, name)
            .set(USERS.SURNAME, surname)
            .set(USERS.LOGIN, login)
            .set(USERS.PHONENUMBER, phoneNumber.filter { it.isDigit() })
            .set(USERS.EMAIL, email)
            .set(USERS.PASSWORD, password)
            .set(USERS.VKLINK, vkLink)
            .set(USERS.ROLE, role.asDbRole())
            .returningResult()
            .fetchOne()
            ?.toUser()

    override fun updateRole(
        user: User,
        newRole: Role,
    ): User? =
        newRole
            .asDbRole()
            ?.let { role ->
                jooqContext.update(USERS)
                    .set(USERS.ROLE, role)
                    .where(USERS.ID.eq(user.id))
                    .returningResult()
                    .fetchOne()
                    ?.toUser()
            }

    override fun updatePassword(
        userID: Int,
        newPassword: String,
    ): User? =
        jooqContext.update(USERS)
            .set(USERS.PASSWORD, newPassword)
            .where(USERS.ID.eq(userID))
            .returningResult()
            .fetchOne()
            ?.toUser()

    private fun selectFromUsers() =
        jooqContext
            .select(
                USERS.ID,
                USERS.NAME,
                USERS.SURNAME,
                USERS.LOGIN,
                USERS.PHONENUMBER,
                USERS.EMAIL,
                USERS.PASSWORD,
                USERS.VKLINK,
                USERS.ROLE
            )
            .from(USERS)
}

private fun Record.toUser(): User? =
    safeLet(
        this[USERS.ID],
        this[USERS.NAME],
        this[USERS.SURNAME],
        this[USERS.LOGIN],
        this[USERS.EMAIL],
        this[USERS.PHONENUMBER],
        this[USERS.PASSWORD],
        this[USERS.ROLE],
    ) { id, name, surname, login, email, phoneNumber, password, role ->
        User(
            id = id,
            name = name.trim(),
            surname = surname,
            login = login.trim(),
            phoneNumber = phoneNumber,
            email = email,
            password = password,
            vkLink = this[USERS.VKLINK],
            role = role.asAppRole(),
        )
    }

private fun Role.asDbRole(): UserRole? =
    when (this) {
        Role.READER -> UserRole.READER
        Role.WRITER -> UserRole.WRITER
        Role.MODERATOR -> UserRole.MODERATOR
        Role.ADMIN -> UserRole.ADMIN
        Role.ANONYMOUS -> null
    }

private fun UserRole.asAppRole(): Role =
    when (this) {
        UserRole.READER -> Role.READER
        UserRole.WRITER -> Role.WRITER
        UserRole.MODERATOR -> Role.MODERATOR
        UserRole.ADMIN -> Role.ADMIN
    }
