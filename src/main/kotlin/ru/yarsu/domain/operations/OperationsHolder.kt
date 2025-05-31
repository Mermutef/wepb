package ru.yarsu.domain.operations

import ru.yarsu.config.AppConfig
import ru.yarsu.domain.dependencies.DatabaseOperations
import ru.yarsu.domain.operations.comments.CommentOperationsHolder
import ru.yarsu.domain.operations.hashtags.HashtagOperationsHolder
import ru.yarsu.domain.operations.media.MediaOperationsHolder
import ru.yarsu.domain.operations.posts.PostOperationsHolder
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

    val postOperations: PostOperationsHolder =
        PostOperationsHolder(
            database.postsOperations,
            database.hashtagOperations,
            database.userOperations,
            database.mediaOperations
        )

    val hashtagOperations: HashtagOperationsHolder =
        HashtagOperationsHolder(database.hashtagOperations, database.postsOperations)

    val commentOperations: CommentOperationsHolder =
        CommentOperationsHolder(
            database.commentOperations,
            database.postsOperations,
            database.userOperations
        )
}
