package ru.yarsu.db.hashtag

import org.jooq.DSLContext
import org.jooq.Record
import ru.yarsu.db.generated.tables.references.HASHTAG
import ru.yarsu.db.generated.tables.references.POSTS
import ru.yarsu.db.utils.safeLet
import ru.yarsu.domain.dependencies.HashtagDatabase
import ru.yarsu.domain.models.Hashtag

class HashtagOperations(
    private val jooqContext: DSLContext,
): HashtagDatabase {
    override fun selectHashtagByID(hashtagId: Int): Hashtag? =
        selectFromHashtags()
            .where(HASHTAG.ID.eq(hashtagId))
            .fetchOne()
            ?.toHashtag()

    override fun selectHashtagByTitle(title: String): Hashtag? =
        selectFromHashtags()
            .where(HASHTAG.TITLE.eq(title))
            .fetchOne()
            ?.toHashtag()

    override fun selectAllHashtags(): List<Hashtag> =
        selectFromHashtags()
            .fetch()
            .mapNotNull { it.toHashtag() }

    override fun insertHashtag(title: String): Hashtag? =
        jooqContext.insertInto(HASHTAG)
            .set(HASHTAG.TITLE, title)
            .returningResult()
            .fetchOne()
            ?.toHashtag()

    override fun updateTitle(hashtagId: Int, newTitle: String): Hashtag? =
        jooqContext.update(HASHTAG)
            .set(HASHTAG.TITLE, newTitle)
            .where(POSTS.ID.eq(hashtagId))
            .returningResult()
            .fetchOne()
            ?.toHashtag()

    private fun selectFromHashtags() =
        jooqContext
            .select(
                HASHTAG.ID,
                HASHTAG.TITLE
            )
            .from(HASHTAG)
}

private fun Record.toHashtag(): Hashtag? =
    safeLet(
        this[HASHTAG.ID],
        this[HASHTAG.TITLE]
    ) { id, title ->
        Hashtag(
            id = id,
            title = title
        )
    }