package ru.yarsu.db

import org.jooq.DSLContext
import ru.yarsu.db.comment.CommentOperations
import ru.yarsu.db.hashtag.HashtagsOperations
import ru.yarsu.db.directions.DirectionOperations
import ru.yarsu.db.media.MediaOperations
import ru.yarsu.db.posts.PostsOperations
import ru.yarsu.db.users.UserOperations
import ru.yarsu.domain.dependencies.DatabaseOperations
import ru.yarsu.domain.dependencies.DirectionsDatabase
import ru.yarsu.domain.dependencies.MediaDatabase
import ru.yarsu.domain.dependencies.UsersDatabase

class DatabaseOperationsHolder (
    jooqContext: DSLContext,
) : DatabaseOperations {
    private val userOperationsInternal = UserOperations(jooqContext)
    private val mediaOperationsInternal = MediaOperations(jooqContext)
    private val postOperationsInternal = PostsOperations(jooqContext)
    private val hashtagOperationsInternal = HashtagsOperations(jooqContext)
    private val directionOperationsInternal = DirectionOperations(jooqContext)
    private val commentOperationsInterval = CommentOperations(jooqContext)

    override val userOperations: UsersDatabase get() = userOperationsInternal
    override val mediaOperations: MediaDatabase get() = mediaOperationsInternal
    override val postsOperations: PostsOperations get() = postOperationsInternal
    override val hashtagOperations: HashtagsOperations get() = hashtagOperationsInternal
    override val directionOperations: DirectionsDatabase get() = directionOperationsInternal
    override val commentOperations: CommentOperations get() = commentOperationsInterval
}
