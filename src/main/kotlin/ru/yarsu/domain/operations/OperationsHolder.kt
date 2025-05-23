package ru.yarsu.domain.operations

import ru.yarsu.config.AppConfig
import ru.yarsu.domain.dependencies.DatabaseOperations
import ru.yarsu.domain.operations.media.MediaOperationsHolder
import ru.yarsu.domain.operations.users.UserOperationsHolder

class OperationsHolder(
    database: DatabaseOperations,
    config: AppConfig,
) {
    val userOperations: UserOperationsHolder = UserOperationsHolder(
        database.userOperations,
        config,
    )

    val mediaOperations: MediaOperationsHolder = MediaOperationsHolder(database.mediaOperations)
}
