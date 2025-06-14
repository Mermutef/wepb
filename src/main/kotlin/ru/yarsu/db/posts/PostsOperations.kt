package ru.yarsu.db.posts

import org.jooq.DSLContext
import org.jooq.Record
import ru.yarsu.db.generated.enums.PostStatus
import ru.yarsu.db.generated.tables.references.POSTS
import ru.yarsu.db.utils.safeLet
import ru.yarsu.domain.dependencies.PostsDatabase
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import java.time.ZonedDateTime

@Suppress("detekt:TooManyFunctions")
class PostsOperations(
    private val jooqContext: DSLContext,
) : PostsDatabase {
    override fun selectPostByID(postID: Int): Post? =
        selectFromPosts()
            .where(POSTS.ID.eq(postID))
            .fetchOne()
            ?.toPost()

    override fun selectPostsByHashtagId(idHashtag: Int): List<Post> =
        selectFromPosts()
            .where(POSTS.HASHTAG.eq(idHashtag))
            .orderBy(POSTS.LAST_MODIFIED_DATE.desc())
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectNNewestPosts(countN: Int): List<Post> =
        selectFromPosts()
            .orderBy(POSTS.LAST_MODIFIED_DATE.desc())
            .limit(countN)
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectPostsByAuthorId(authorId: Int): List<Post> =
        selectFromPosts()
            .where(POSTS.AUTHORID.eq(authorId))
            .orderBy(POSTS.LAST_MODIFIED_DATE.desc())
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectPostsByModeratorId(moderatorId: Int): List<Post> =
        selectFromPosts()
            .where(POSTS.MODERATORID.eq(moderatorId))
            .orderBy(POSTS.LAST_MODIFIED_DATE.desc())
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectPostsByStatus(status: Status): List<Post> =
        selectFromPosts()
            .where(POSTS.STATUS.eq(status.asDbStatus()))
            .orderBy(POSTS.LAST_MODIFIED_DATE.desc())
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectPostsByTimeInterval(
        startDate: ZonedDateTime,
        endDate: ZonedDateTime,
    ): List<Post> =
        selectFromPosts()
            .where(POSTS.LAST_MODIFIED_DATE.between(startDate.toOffsetDateTime(), endDate.toOffsetDateTime()))
            .orderBy(POSTS.LAST_MODIFIED_DATE.desc())
            .fetch()
            .map { it.toPost() }

    override fun selectAllPosts(): List<Post> =
        selectFromPosts()
            .orderBy(POSTS.LAST_MODIFIED_DATE.desc())
            .fetch()
            .mapNotNull { it.toPost() }

    override fun insertPost(
        title: String,
        preview: String,
        content: String,
        hashtagId: Int,
        eventDate: ZonedDateTime?,
        creationDate: ZonedDateTime,
        lastModifiedDate: ZonedDateTime,
        authorId: Int,
        moderatorId: Int?,
        status: Status,
    ): Post? =
        status
            .asDbStatus()
            .let { dbStatus ->
                jooqContext.insertInto(POSTS)
                    .set(POSTS.TITLE, title)
                    .set(POSTS.PREVIEW, preview)
                    .set(POSTS.CONTENT, content)
                    .set(POSTS.HASHTAG, hashtagId)
                    .set(POSTS.EVENT_DATE, eventDate?.toOffsetDateTime())
                    .set(POSTS.CREATION_DATE, creationDate.toOffsetDateTime())
                    .set(POSTS.LAST_MODIFIED_DATE, lastModifiedDate.toOffsetDateTime())
                    .set(POSTS.AUTHORID, authorId)
                    .set(POSTS.MODERATORID, moderatorId)
                    .set(POSTS.STATUS, dbStatus)
                    .returningResult()
                    .fetchOne()
                    ?.toPost()
            }

    override fun updateTitle(
        postID: Int,
        newTitle: String,
        dateNow: ZonedDateTime,
    ): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.TITLE, newTitle)
            .set(POSTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updatePreview(
        postID: Int,
        newPreview: String,
        dateNow: ZonedDateTime,
    ): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.PREVIEW, newPreview)
            .set(POSTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateContent(
        postID: Int,
        newContent: String,
        dateNow: ZonedDateTime,
    ): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.CONTENT, newContent)
            .set(POSTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateHashtagId(
        postID: Int,
        newHashtagId: Int,
        dateNow: ZonedDateTime,
    ): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.HASHTAG, newHashtagId)
            .set(POSTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateEventDate(
        postID: Int,
        newEventBody: ZonedDateTime,
        dateNow: ZonedDateTime,
    ): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.EVENT_DATE, newEventBody.toOffsetDateTime())
            .set(POSTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateAuthorId(
        postID: Int,
        newAuthorId: Int,
        dateNow: ZonedDateTime,
    ): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.AUTHORID, newAuthorId)
            .set(POSTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateModeratorId(
        postID: Int,
        newModeratorId: Int,
        dateNow: ZonedDateTime,
    ): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.MODERATORID, newModeratorId)
            .set(POSTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateStatus(
        post: Post,
        newStatus: Status,
        dateNow: ZonedDateTime,
    ): Post? {
        if (newStatus == Status.PUBLISHED) {
            return newStatus
                .asDbStatus()
                .let { status ->
                    jooqContext.update(POSTS)
                        .set(POSTS.STATUS, status)
                        .set(POSTS.CREATION_DATE, dateNow.toOffsetDateTime())
                        .set(POSTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
                        .where(POSTS.ID.eq(post.id))
                        .returningResult()
                        .fetchOne()
                        ?.toPost()
                }
        } else {
            return newStatus
                .asDbStatus()
                .let { status ->
                    jooqContext.update(POSTS)
                        .set(POSTS.STATUS, status)
                        .set(POSTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
                        .where(POSTS.ID.eq(post.id))
                        .returningResult()
                        .fetchOne()
                        ?.toPost()
                }
        }
    }

    override fun updatePost(
        postID: Int,
        newTitle: String,
        newPreview: String,
        newContent: String,
        newHashtagId: Int,
        newEventDate: ZonedDateTime?,
        newAuthorId: Int,
        newModeratorId: Int?,
        dateNow: ZonedDateTime,
    ): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.TITLE, newTitle)
            .set(POSTS.PREVIEW, newPreview)
            .set(POSTS.CONTENT, newContent)
            .set(POSTS.HASHTAG, newHashtagId)
            .set(POSTS.EVENT_DATE, newEventDate?.toOffsetDateTime())
            .set(POSTS.AUTHORID, newAuthorId)
            .set(POSTS.MODERATORID, newModeratorId)
            .set(POSTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    private fun selectFromPosts() =
        jooqContext
            .select(
                POSTS.ID,
                POSTS.TITLE,
                POSTS.PREVIEW,
                POSTS.CONTENT,
                POSTS.HASHTAG,
                POSTS.EVENT_DATE,
                POSTS.CREATION_DATE,
                POSTS.LAST_MODIFIED_DATE,
                POSTS.AUTHORID,
                POSTS.MODERATORID,
                POSTS.STATUS
            )
            .from(POSTS)
}

private fun Record.toPost(): Post? =
    safeLet(
        this[POSTS.ID],
        this[POSTS.TITLE],
        this[POSTS.PREVIEW],
        this[POSTS.CONTENT],
        this[POSTS.HASHTAG],
        this[POSTS.CREATION_DATE],
        this[POSTS.LAST_MODIFIED_DATE],
        this[POSTS.AUTHORID],
        this[POSTS.STATUS]
    ) { id, title, preview, content, hashtagId, creationDate, lastModifiedDate, authorId, status ->
        Post(
            id = id,
            title = title,
            preview = preview,
            content = content,
            hashtagId = hashtagId,
            eventDate = this[POSTS.EVENT_DATE]?.toZonedDateTime(),
            creationDate = creationDate.toZonedDateTime(),
            lastModifiedDate = lastModifiedDate.toZonedDateTime(),
            authorId = authorId,
            moderatorId = this[POSTS.MODERATORID],
            status = status.asAppStatus(),
        )
    }

private fun Status.asDbStatus(): PostStatus =
    when (this) {
        Status.DRAFT -> PostStatus.DRAFT
        Status.HIDDEN -> PostStatus.HIDDEN
        Status.PUBLISHED -> PostStatus.PUBLISHED
        Status.MODERATION -> PostStatus.MODERATION
    }

private fun PostStatus.asAppStatus(): Status =
    when (this) {
        PostStatus.DRAFT -> Status.DRAFT
        PostStatus.HIDDEN -> Status.HIDDEN
        PostStatus.PUBLISHED -> Status.PUBLISHED
        PostStatus.MODERATION -> Status.MODERATION
    }
