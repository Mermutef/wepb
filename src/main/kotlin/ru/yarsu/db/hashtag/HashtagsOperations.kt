package ru.yarsu.db.hashtag

import org.jooq.DSLContext
import org.jooq.Record
import ru.yarsu.db.generated.tables.references.HASHTAGS
import ru.yarsu.db.utils.safeLet
import ru.yarsu.domain.dependencies.HashtagsDatabase
import ru.yarsu.domain.models.Hashtag

class HashtagsOperations(
    private val jooqContext: DSLContext,
) : HashtagsDatabase {
    override fun selectHashtagByID(hashtagId: Int): Hashtag? =
        selectFromHashtags()
            .where(HASHTAGS.ID.eq(hashtagId))
            .fetchOne()
            ?.toHashtag()

    override fun selectHashtagByTitle(title: String): Hashtag? =
        selectFromHashtags()
            .where(HASHTAGS.TITLE.eq(title))
            .fetchOne()
            ?.toHashtag()

    override fun selectAllHashtags(): List<Hashtag> =
        selectFromHashtags()
            .fetch()
            .mapNotNull { it.toHashtag() }

    override fun insertHashtag(title: String): Hashtag? =
        jooqContext.insertInto(HASHTAGS)
            .set(HASHTAGS.TITLE, title)
            .returningResult()
            .fetchOne()
            ?.toHashtag()

    override fun updateTitle(
        hashtagId: Int,
        newTitle: String,
    ): Hashtag? =
        jooqContext.update(HASHTAGS)
            .set(HASHTAGS.TITLE, newTitle)
            .where(HASHTAGS.ID.eq(hashtagId))
            .returningResult()
            .fetchOne()
            ?.toHashtag()

    override fun deleteHashtagById(hashtagId: Int): Int? =
        jooqContext.delete(HASHTAGS)
            .where(HASHTAGS.ID.eq(hashtagId))
            .execute()

    private fun selectFromHashtags() =
        jooqContext
            .select(
                HASHTAGS.ID,
                HASHTAGS.TITLE
            )
            .from(HASHTAGS)
}

private fun Record.toHashtag(): Hashtag? =
    safeLet(
        this[HASHTAGS.ID],
        this[HASHTAGS.TITLE]
    ) { id, title ->
        Hashtag(
            id = id,
            title = title
        )
    }
