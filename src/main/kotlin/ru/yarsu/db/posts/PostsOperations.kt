package ru.yarsu.db.posts

import org.jooq.DSLContext
import org.jooq.Record
import ru.yarsu.db.generated.enums.PostStatus
import ru.yarsu.db.generated.tables.references.POSTS
import ru.yarsu.db.utils.safeLet
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.dependencies.PostsDatabase
import ru.yarsu.domain.models.Post
import java.time.LocalDateTime
import java.time.ZonedDateTime

class PostsOperations(
    private val jooqContext: DSLContext,
): PostsDatabase {
    override fun selectPostByID(postID: Int): Post? =
        selectFromPosts()
            .where(POSTS.ID.eq(postID))
            .fetchOne()
            ?.toPost()

    override fun selectPostsByIdHashtag(idHashtag: Int): List<Post> =
        selectFromPosts()
            .where(POSTS.HASHTAG.eq(idHashtag))
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectNNewPosts(countN: Int): List<Post> =
        selectFromPosts()
            .orderBy(POSTS.LAST_MODIFIED_DATE.desc())
            .limit(countN)
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectPostsByAuthorId(authorId: Int): List<Post> =
        selectFromPosts()
            .where(POSTS.AUTHORID.eq(authorId))
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectPostsByModeratorId(moderatorId: Int): List<Post> =
        selectFromPosts()
            .where(POSTS.MODERATORID.eq(moderatorId))
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectPostByStatus(status: Status): List<Post> =
        selectFromPosts()
            .where(POSTS.STATUS.eq(status.asDbStatus()))
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectPostsByTimeInterval(startDate: LocalDateTime, endDate: LocalDateTime): List<Post> =
        selectFromPosts()
            .where(POSTS.LAST_MODIFIED_DATE.between(startDate, endDate))
            .orderBy(POSTS.LAST_MODIFIED_DATE.desc())
            .fetch()
            .map { it.toPost() }

//    override fun selectPostsByTimeInterval(startDate: ZonedDateTime, endDate: ZonedDateTime): List<Post> =
//        selectFromPosts()
//            .where(POSTS.LAST_MODIFIED_DATE.between(startDate, endDate))
//            .orderBy(POSTS.LAST_MODIFIED_DATE.desc())
//            .fetch()
//            .map { it.toPost() }

    override fun selectAllPosts(): List<Post> =
        selectFromPosts()
            .fetch()
            .mapNotNull { it.toPost() }

    override fun insertPost(
        title: String,
        preview: String,
        content: String,
        hashtagId: Int,
        eventDate: LocalDateTime?,
        creationDate: LocalDateTime,
        lastModifiedDate: LocalDateTime,
        authorId: Int,
        moderatorId: Int,
        status: Status
    ): Post? =
        status
            .asDbStatus()
            .let { dbStatus ->
                jooqContext.insertInto(POSTS)
                    .set(POSTS.TITLE, title)
                    .set(POSTS.PREVIEW, preview)
                    .set(POSTS.CONTENT, content)
                    .set(POSTS.HASHTAG, hashtagId)
                    .set(POSTS.EVENT_DATE, eventDate)
                    .set(POSTS.CREATION_DATE, creationDate)
                    .set(POSTS.LAST_MODIFIED_DATE, lastModifiedDate)
                    .set(POSTS.AUTHORID, authorId)
                    .set(POSTS.MODERATORID, moderatorId)
                    .set(POSTS.STATUS, dbStatus)
                    .returningResult()
                    .fetchOne()
                    ?.toPost()
            }

    override fun updateTitle(postID: Int, newTitle: String): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.TITLE, newTitle)
            .set(POSTS.LAST_MODIFIED_DATE, LocalDateTime.now())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updatePreview(postID: Int, newPreview: String): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.PREVIEW, newPreview)
            .set(POSTS.LAST_MODIFIED_DATE, LocalDateTime.now())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateContent(postID: Int, newContent: String): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.CONTENT, newContent)
            .set(POSTS.LAST_MODIFIED_DATE, LocalDateTime.now())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateHashtagId(postID: Int, newHashtagId: Int): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.HASHTAG, newHashtagId)
            .set(POSTS.LAST_MODIFIED_DATE, LocalDateTime.now())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateEventDate(postID: Int, newEventBody: LocalDateTime): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.EVENT_DATE, newEventBody)
            .set(POSTS.LAST_MODIFIED_DATE, LocalDateTime.now())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateAuthorId(postID: Int, newAuthorId: Int): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.AUTHORID, newAuthorId)
            .set(POSTS.LAST_MODIFIED_DATE, LocalDateTime.now())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()


    override fun updateModeratorId(postID: Int, newModeratorId: Int): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.MODERATORID, newModeratorId)
            .set(POSTS.LAST_MODIFIED_DATE, LocalDateTime.now())
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()


    override fun updateStatus(post: Post, newStatus: Status): Post? {
        if (newStatus == Status.PUBLISHED)
        {
            return newStatus
                .asDbStatus()
                .let { status ->
                    jooqContext.update(POSTS)
                        .set(POSTS.STATUS, status)
                        .set(POSTS.CREATION_DATE, LocalDateTime.now())
                        .set(POSTS.LAST_MODIFIED_DATE, LocalDateTime.now())
                        .where(POSTS.ID.eq(post.id))
                        .returningResult()
                        .fetchOne()
                        ?.toPost()
                }
        }
        else
        {
            return newStatus
                .asDbStatus()
                .let { status ->
                    jooqContext.update(POSTS)
                        .set(POSTS.STATUS, status)
                        .set(POSTS.LAST_MODIFIED_DATE, LocalDateTime.now())
                        .where(POSTS.ID.eq(post.id))
                        .returningResult()
                        .fetchOne()
                        ?.toPost()
                }
        }
    }

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
        this[POSTS.MODERATORID],
        this[POSTS.STATUS]
    ) { id, title, preview, content, hashtagId, creationDate, lastModifiedDate, authorId, moderatorId, status ->
        Post(
            id = id,
            title = title,
            preview = preview,
            content = content,
            hashtagId = hashtagId,
            eventDate = this[POSTS.EVENT_DATE],
            creationDate = creationDate,
            lastModifiedDate = lastModifiedDate,
            authorId = authorId,
            moderatorId = moderatorId,
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
