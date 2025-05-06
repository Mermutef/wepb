package ru.yarsu.db

import org.jooq.DSLContext
import ru.yarsu.db.generated.tables.references.HASHTAGS
import ru.yarsu.db.hashtag.HashtagOperations
import ru.yarsu.db.media.MediaOperations
import ru.yarsu.db.posts.PostsOperations
import ru.yarsu.db.users.UserOperations
import ru.yarsu.domain.dependencies.DatabaseOperations
import ru.yarsu.domain.dependencies.HashtagDatabase
import ru.yarsu.domain.dependencies.MediaDatabase
import ru.yarsu.domain.dependencies.UsersDatabase

class DatabaseOperationsHolder (
    jooqContext: DSLContext,
) : DatabaseOperations {
    private val userOperationsInternal = UserOperations(jooqContext)
    private val mediaOperationsInternal = MediaOperations(jooqContext)
    private val postOperationsInternal = PostsOperations(jooqContext)
    private val hashtagOperationsInternal = HashtagOperations(jooqContext)

    override val userOperations: UsersDatabase get() = userOperationsInternal
    override val mediaOperations: MediaDatabase get() = mediaOperationsInternal
    override val postsOperations: PostsOperations get() = postOperationsInternal
    override val hashtagOperations: HashtagOperations get() = hashtagOperationsInternal
}
