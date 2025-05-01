package ru.yarsu.db.posts

import org.jooq.DSLContext
import org.jooq.Record
import ru.yarsu.db.generated.tables.references.HASHTAG
import ru.yarsu.db.generated.tables.references.POSTS
import ru.yarsu.db.generated.tables.references.POST_AND_HASHTAG
import ru.yarsu.db.utils.safeLet
import ru.yarsu.domain.dependencies.PostAndHashtagDatabase


class PostAndHashtagOperations (
    private val jooqContext: DSLContext,
): PostAndHashtagDatabase {
    override fun selectPostAndHashtagByID(id: Int): Pair<Int, Int>? =
        selectFromPostAndHashtag()
            .where(POSTS.ID.eq(id))
            .fetchOne()
            ?.toPostAndHashtag()

    override fun selectPostAndHashtagByPostID(postId: Int): List<Int> =
        selectFromPostAndHashtag()
            .where(POSTS.ID.eq(id))
            .fetchOne()
            ?.toPostAndHashtag()


    override fun selectPostAndHashtagByHashtagID(hashtagId: Int): List<Int> {
        TODO("Not yet implemented")
    }

    override fun selectAllPostsAndHashtags(): List<Pair<Int, Int>> {
        TODO("Not yet implemented")
    }

    override fun insertPostAndHashtag(postId: Int, hashtagId: Int): Pair<Int, Int>? {
        TODO("Not yet implemented")
    }

    override fun updateHashtagId(id: Int, newHashtagId: Int): Pair<Int, Int> {
        TODO("Not yet implemented")
    }

    private fun selectFromPostAndHashtag() =
        jooqContext
            .select(
                POST_AND_HASHTAG.ID,
                POST_AND_HASHTAG.POSTID,
                POST_AND_HASHTAG.HASHTAGID
            )
            .from(POST_AND_HASHTAG)
}

private fun Record.toPostAndHashtag(): Pair<Int, Int>? =
    safeLet(
        this[POST_AND_HASHTAG.ID],
        this[POST_AND_HASHTAG.POSTID],
        this[POST_AND_HASHTAG.HASHTAGID]
    ) { id, postId, hashtagId ->
        Pair(postId, hashtagId)
    }

