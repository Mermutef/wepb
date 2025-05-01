package ru.yarsu.domain.dependencies

import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

interface PostsDatabase {
    fun selectPostByID(postID: Int): Post?

    fun selectPostByTitle(title: String): Post?

    fun selectPostByStatus(status: Status): List<Post>

    fun selectAllPosts(): List<Post>

    fun insertPost(
        title: String,
        preview: String,
        textBody: String,
        eventDate: LocalDateTime?,
        authorId: Int,
        moderatorId: Int,
        status: Status
    ): Post?

    fun updateTitle(
        postID: Int,
        newTitle: String,
    ): Post?

    fun updatePreview(
        postID: Int,
        newPreview: String,
    ): Post?

    fun updateTextBody(
        postID: Int,
        newTextBody: String,
    ): Post?

    fun updateEventDate(
        postID: Int,
        newEventBody: LocalDateTime,
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