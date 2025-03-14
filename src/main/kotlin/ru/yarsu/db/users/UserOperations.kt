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
        selectFromUsers(jooqContext)
            .where(USERS.ID.eq(userID))
            .fetchOne()
            ?.toUser()

    override fun selectUserByName(userName: String): User? =
        selectFromUsers(jooqContext)
            .where(USERS.NAME.eq(userName))
            .fetchOne()
            ?.toUser()

    override fun selectUserByEmail(email: String): User? =
        selectFromUsers(jooqContext)
            .where(USERS.EMAIL.eq(email))
            .fetchOne()
            ?.toUser()

    override fun selectUsersByRole(userRole: Role): List<User> =
        selectFromUsers(jooqContext)
            .where(USERS.ROLE.eq(userRole.asDbRole()))
            .fetch()
            .mapNotNull { record: Record ->
                record.toUser()
            }

    override fun selectAllUsers(): List<User> =
        selectFromUsers(jooqContext)
            .fetch()
            .mapNotNull { record: Record ->
                record.toUser()
            }

    override fun insertUser(
        name: String,
        email: String,
        pass: String,
        role: Role,
    ): User? =
        jooqContext.insertInto(USERS)
            .set(USERS.NAME, name)
            .set(USERS.EMAIL, email)
            .set(USERS.PASSWORD, pass)
            .set(USERS.ROLE, UserRole.valueOf(role.toString()))
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

    private fun selectFromUsers(jooqContext: DSLContext) =
        jooqContext
            .select(
                USERS.ID,
                USERS.NAME,
                USERS.EMAIL,
                USERS.PASSWORD,
                USERS.ROLE,
            )
            .from(USERS)
}

private fun Record.toUser(): User? =
    safeLet(
        this[USERS.ID],
        this[USERS.NAME],
        this[USERS.EMAIL],
        this[USERS.PASSWORD],
        this[USERS.ROLE],
    ) { id, name, email, password, role ->
        User(
            id,
            name,
            email,
            password,
            Role.valueOf(role.toString())
        )
    }

private fun Role.asDbRole(): UserRole? =
    when (this) {
        Role.MODERATOR -> UserRole.MODERATOR
        Role.AUTHORIZED -> UserRole.AUTHORIZED
        Role.ADMIN -> UserRole.ADMIN
        Role.ANONYMOUS -> null
    }
