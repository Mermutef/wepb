package ru.yarsu.domain.dependencies

import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import java.time.ZonedDateTime

@Suppress("detekt:TooManyFunctions")
interface PostsDatabase {
    fun selectPostByID(postID: Int): Post?

    fun selectPostsByHashtagId(idHashtag: Int): List<Post>

    fun selectNNewestPosts(countN: Int): List<Post>

    fun selectPostsByAuthorId(authorId: Int): List<Post>

    fun selectPostsByModeratorId(moderatorId: Int): List<Post>

    fun selectPostsByStatus(status: Status): List<Post>

    fun selectPostsByTimeInterval(
        startDate: ZonedDateTime,
        endDate: ZonedDateTime,
    ): List<Post>

    fun selectAllPosts(): List<Post>

    @Suppress("detekt:LongParameterList")
    fun insertPost(
        title: String,
        preview: String,
        content: String,
        hashtagId: Int,
        eventDate: ZonedDateTime?,
        creationDate: ZonedDateTime = ZonedDateTime.now(),
        lastModifiedDate: ZonedDateTime = ZonedDateTime.now(),
        authorId: Int,
        moderatorId: Int?,
        status: Status,
    ): Post?

    fun updateTitle(
        postID: Int,
        newTitle: String,
        dateNow: ZonedDateTime = ZonedDateTime.now(),
    ): Post?

    fun updatePreview(
        postID: Int,
        newPreview: String,
        dateNow: ZonedDateTime = ZonedDateTime.now(),
    ): Post?

    fun updateContent(
        postID: Int,
        newContent: String,
        dateNow: ZonedDateTime = ZonedDateTime.now(),
    ): Post?

    fun updateHashtagId(
        postID: Int,
        newHashtagId: Int,
        dateNow: ZonedDateTime = ZonedDateTime.now(),
    ): Post?

    fun updateEventDate(
        postID: Int,
        newEventBody: ZonedDateTime,
        dateNow: ZonedDateTime = ZonedDateTime.now(),
    ): Post?

    fun updateAuthorId(
        postID: Int,
        newAuthorId: Int,
        dateNow: ZonedDateTime = ZonedDateTime.now(),
    ): Post?

    fun updateModeratorId(
        postID: Int,
        newModeratorId: Int,
        dateNow: ZonedDateTime = ZonedDateTime.now(),
    ): Post?

    fun updateStatus(
        post: Post,
        newStatus: Status,
        dateNow: ZonedDateTime = ZonedDateTime.now(),
    ): Post?

    @Suppress("detekt:LongParameterList")
    fun updatePost(
        postID: Int,
        newTitle: String,
        newPreview: String,
        newContent: String,
        newHashtagId: Int,
        newEventDate: ZonedDateTime?,
        newAuthorId: Int,
        newModeratorId: Int?,
        dateNow: ZonedDateTime = ZonedDateTime.now(),
    ): Post?
}
