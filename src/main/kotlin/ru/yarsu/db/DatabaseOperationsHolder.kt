package ru.yarsu.db

import org.jooq.DSLContext
import ru.yarsu.db.media.MediaOperations
import ru.yarsu.db.users.UserOperations
import ru.yarsu.domain.dependencies.DatabaseOperations
import ru.yarsu.domain.dependencies.MediaDatabase
import ru.yarsu.domain.dependencies.UsersDatabase

class DatabaseOperationsHolder (
    jooqContext: DSLContext,
) : DatabaseOperations {
    private val userOperationsInternal = UserOperations(jooqContext)
    private val mediaOperationsInternal = MediaOperations(jooqContext)

    override val userOperations: UsersDatabase get() = userOperationsInternal
    override val mediaOperations: MediaDatabase get() = mediaOperationsInternal
}
