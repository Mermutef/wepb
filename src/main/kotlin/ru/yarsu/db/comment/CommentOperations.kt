package ru.yarsu.db.comment

import org.jooq.DSLContext
import org.jooq.Record
import ru.yarsu.db.generated.tables.references.COMMENTS
import ru.yarsu.db.utils.safeLet
import ru.yarsu.domain.dependencies.CommentsDatabase
import ru.yarsu.domain.models.Comment
import java.time.ZonedDateTime

class CommentOperations (
    private val jooqContext: DSLContext,
) : CommentsDatabase {
    override fun selectCommentById(commentId: Int): Comment? =
        selectFromComments()
            .where(COMMENTS.ID.eq(commentId))
            .fetchOne()
            ?.toComment()

    override fun selectPublishedCommentsInPost(postId: Int): List<Comment> =
        selectFromComments()
            .where(COMMENTS.POSTID.eq(postId), COMMENTS.IS_HIDDEN.eq(false))
            .orderBy(COMMENTS.CREATION_DATE.desc())
            .mapNotNull { it.toComment() }

    override fun selectHiddenCommentsInPost(postId: Int): List<Comment> =
        selectFromComments()
            .where(COMMENTS.POSTID.eq(postId), COMMENTS.IS_HIDDEN.eq(true))
            .orderBy(COMMENTS.CREATION_DATE.desc())
            .mapNotNull { it.toComment() }

    override fun selectHiddenCommentsOfUserInPost(
        postId: Int,
        authorId: Int,
    ): List<Comment> =
        selectFromComments()
            .where(COMMENTS.POSTID.eq(postId), COMMENTS.AUTHORID.eq(authorId), COMMENTS.IS_HIDDEN.eq(true))
            .orderBy(COMMENTS.CREATION_DATE.desc())
            .mapNotNull { it.toComment() }

    override fun insertComment(
        content: String,
        authorId: Int,
        postId: Int,
        creationDate: ZonedDateTime,
        lastModifiedDate: ZonedDateTime,
        isHidden: Boolean,
    ): Comment? =
        jooqContext.insertInto(COMMENTS)
            .set(COMMENTS.CONTENT, content)
            .set(COMMENTS.AUTHORID, authorId)
            .set(COMMENTS.POSTID, postId)
            .set(COMMENTS.CREATION_DATE, creationDate.toOffsetDateTime())
            .set(COMMENTS.LAST_MODIFIED_DATE, lastModifiedDate.toOffsetDateTime())
            .set(COMMENTS.IS_HIDDEN, isHidden)
            .returningResult()
            .fetchOne()
            ?.toComment()

    override fun hideComment(commentId: Int): Comment? =
        jooqContext.update(COMMENTS)
            .set(COMMENTS.IS_HIDDEN, true)
            .where(COMMENTS.ID.eq(commentId))
            .returningResult()
            .fetchOne()
            ?.toComment()

    override fun publishComment(commentId: Int): Comment? =
        jooqContext.update(COMMENTS)
            .set(COMMENTS.IS_HIDDEN, false)
            .where(COMMENTS.ID.eq(commentId))
            .returningResult()
            .fetchOne()
            ?.toComment()

    override fun updateContent(
        commentId: Int,
        newContent: String,
        dateNow: ZonedDateTime,
    ): Comment? =
        jooqContext.update(COMMENTS)
            .set(COMMENTS.CONTENT, newContent)
            .set(COMMENTS.LAST_MODIFIED_DATE, dateNow.toOffsetDateTime())
            .where(COMMENTS.ID.eq(commentId))
            .returningResult()
            .fetchOne()
            ?.toComment()

    private fun selectFromComments() =
        jooqContext
            .select(
                COMMENTS.ID,
                COMMENTS.CONTENT,
                COMMENTS.AUTHORID,
                COMMENTS.POSTID,
                COMMENTS.CREATION_DATE,
                COMMENTS.LAST_MODIFIED_DATE,
                COMMENTS.IS_HIDDEN
            )
            .from(COMMENTS)
}

private fun Record.toComment(): Comment? =
    safeLet(
        this[COMMENTS.ID],
        this[COMMENTS.CONTENT],
        this[COMMENTS.AUTHORID],
        this[COMMENTS.POSTID],
        this[COMMENTS.CREATION_DATE],
        this[COMMENTS.LAST_MODIFIED_DATE],
        this[COMMENTS.IS_HIDDEN]
    ) { id, content, authorId, postId, creationDate, lastModifiedDate, isHidden ->
        Comment(
            id = id,
            content = content,
            authorId = authorId,
            postId = postId,
            creationDate = creationDate.toZonedDateTime(),
            lastModifiedDate = lastModifiedDate.toZonedDateTime(),
            isHidden = isHidden
        )
    }
