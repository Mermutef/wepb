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

class PostsOperations(
    private val jooqContext: DSLContext,
): PostsDatabase {
    override fun selectPostByID(postID: Int): Post? =
        selectFromPosts()
            .where(POSTS.ID.eq(postID))
            .fetchOne()
            ?.toPost()

    override fun selectPostByTitle(title: String): Post? =
        selectFromPosts()
            .where(POSTS.TITLE.eq(title))
            .fetchOne()
            ?.toPost()

    override fun selectPostByStatus(status: Status): List<Post> =
        selectFromPosts()
            .where(POSTS.STATUS.eq(status.asDbStatus()))
            .fetch()
            .mapNotNull { it.toPost() }

    override fun selectAllPosts(): List<Post> =
        selectFromPosts()
            .fetch()
            .mapNotNull { it.toPost() }

    override fun insertPost(
        title: String,
        preview: String,
        textBody: String,
        eventDate: LocalDateTime?,
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
                    .set(POSTS.TEXT_BODY, textBody)
                    .set(POSTS.EVENT_DATE, eventDate)
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
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updatePreview(postID: Int, newPreview: String): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.PREVIEW, newPreview)
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()


    override fun updateTextBody(postID: Int, newTextBody: String): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.TEXT_BODY, newTextBody)
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateEventDate(postID: Int, newEventBody: LocalDateTime): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.EVENT_DATE, newEventBody)
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()

    override fun updateAuthorId(postID: Int, newAuthorId: Int): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.AUTHORID, newAuthorId)
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()


    override fun updateModeratorId(postID: Int, newModeratorId: Int): Post? =
        jooqContext.update(POSTS)
            .set(POSTS.MODERATORID, newModeratorId)
            .where(POSTS.ID.eq(postID))
            .returningResult()
            .fetchOne()
            ?.toPost()


    override fun updateStatus(post: Post, newStatus: Status): Post? =
        newStatus
            .asDbStatus()
            .let { status ->
                jooqContext.update(POSTS)
                    .set(POSTS.STATUS, status)
                    .where(POSTS.ID.eq(post.id))
                    .returningResult()
                    .fetchOne()
                    ?.toPost()
            }

    private fun selectFromPosts() =
        jooqContext
            .select(
                POSTS.ID,
                POSTS.TITLE,
                POSTS.PREVIEW,
                POSTS.TEXT_BODY,
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
        this[POSTS.TEXT_BODY],
        this[POSTS.CREATION_DATE],
        this[POSTS.LAST_MODIFIED_DATE],
        this[POSTS.AUTHORID],
        this[POSTS.MODERATORID],
        this[POSTS.STATUS]
    ) { id, title, preview, textBody, creationDate, lastModifiedDate, authorId, moderatorId, status ->
        Post(
            id = id,
            title = title,
            preview = preview,
            filePreview = null,
            content = textBody,
            mediaFiles = listOf(),
            hashtag = listOf(),
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
