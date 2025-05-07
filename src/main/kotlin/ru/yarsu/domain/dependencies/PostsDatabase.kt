package ru.yarsu.domain.dependencies

import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Post
import java.time.ZonedDateTime

@Suppress("detekt:TooManyFunctions")
interface PostsDatabase {
    fun selectPostByID(postID: Int): Post?

    fun selectPostsByIdHashtag(idHashtag: Int): List<Post>

    fun selectNNewPosts(countN: Int): List<Post>

    fun selectPostsByAuthorId(authorId: Int): List<Post>

    fun selectPostsByModeratorId(moderatorId: Int): List<Post>

    fun selectPostByStatus(status: Status): List<Post>

//    fun selectPostsByTimeInterval(startDate: ZonedDateTime, endDate: ZonedDateTime): List<Post>

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
        creationDate: ZonedDateTime,
        lastModifiedDate: ZonedDateTime,
        authorId: Int,
        moderatorId: Int,
        status: Status,
    ): Post?

    fun updateTitle(
        postID: Int,
        newTitle: String,
    ): Post?

    fun updatePreview(
        postID: Int,
        newPreview: String,
    ): Post?

    fun updateContent(
        postID: Int,
        newContent: String,
    ): Post?

    fun updateHashtagId(
        postID: Int,
        newHashtagId: Int,
    ): Post?

    fun updateEventDate(
        postID: Int,
        newEventBody: ZonedDateTime,
    ): Post?

    fun updateAuthorId(
        postID: Int,
        newAuthorId: Int,
    ): Post?

    fun updateModeratorId(
        postID: Int,
        newModeratorId: Int,
    ): Post?

    fun updateStatus(
        post: Post,
        newStatus: Status,
    ): Post?
}
