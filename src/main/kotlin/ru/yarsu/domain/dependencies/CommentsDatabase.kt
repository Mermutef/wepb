package ru.yarsu.domain.dependencies

import ru.yarsu.domain.models.Comment
import java.time.ZonedDateTime

interface CommentsDatabase {
    fun selectPublishedCommentsInPost(postId: Int): List<Comment>

    fun selectHiddenCommentsInPost(postId: Int): List<Comment>

    fun selectHiddenCommentsOfUserInPost(postId: Int, authorId: Int): List<Comment>

    fun insertComment(
        content: String,
        authorId: Int,
        postId: Int,
        creationDate: ZonedDateTime = ZonedDateTime.now(),
        lastModifiedDate: ZonedDateTime = ZonedDateTime.now(),
        isHidden: Boolean = false
    ): Comment?

    fun hideComment(commentId: Int): Comment?

    fun publishComment(commentId: Int): Comment?

    fun updateContent(
        commentId: Int,
        newContent: String,
        dateNow: ZonedDateTime = ZonedDateTime.now()): Comment?
}