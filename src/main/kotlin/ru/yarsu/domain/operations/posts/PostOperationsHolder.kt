package ru.yarsu.domain.operations.posts

import dev.forkhandles.result4k.Result4k
import ru.yarsu.domain.dependencies.HashtagsDatabase
import ru.yarsu.domain.dependencies.MediaDatabase
import ru.yarsu.domain.dependencies.PostsDatabase
import ru.yarsu.domain.dependencies.UsersDatabase
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import java.time.ZonedDateTime

class PostOperationsHolder(
    private val postsDatabase: PostsDatabase,
    private val hashtagsDatabase: HashtagsDatabase,
    private val userDatabase: UsersDatabase,
    private val mediaDatabase: MediaDatabase,
) {

    val fetchAllPosts: () -> Result4k<List<Post>, PostFetchingError> = FetchAllPosts { postsDatabase.selectAllPosts() }

    val fetchPostById: (Int) -> Result4k<Post, PostFetchingError> =
        FetchPostById { postID: Int -> postsDatabase.selectPostByID(postID) }

    val fetchPostsByHashtagId: (Int) -> Result4k<List<Post>, PostFetchingError> =
        FetchPostsByIdHashtag { hashtagId: Int -> postsDatabase.selectPostsByHashtagId(hashtagId) }

    val fetchNNewestPosts: (Int) -> Result4k<List<Post>, PostFetchingError> =
        FetchNNewestPosts { countN: Int -> postsDatabase.selectNNewestPosts(countN) }

    val fetchPostsByAuthorId: (Int) -> Result4k<List<Post>, PostFetchingError> =
        FetchPostsByAuthorId { authorId: Int -> postsDatabase.selectPostsByAuthorId(authorId) }

    val fetchPostsByModeratorId: (Int) -> Result4k<List<Post>, PostFetchingError> =
        FetchPostsByModeratorId { moderatorId: Int -> postsDatabase.selectPostsByModeratorId(moderatorId) }

    val fetchPostsByStatus: (Status) -> Result4k<List<Post>, PostFetchingError> =
        FetchPostByStatus { status: Status -> postsDatabase.selectPostsByStatus(status) }

    val fetchPostsByTimeInterval: (ZonedDateTime, ZonedDateTime) -> Result4k<List<Post>, PostFetchingError> =
        FetchPostsByTimeInterval { startDate: ZonedDateTime, endDate: ZonedDateTime ->
            postsDatabase.selectPostsByTimeInterval(startDate, endDate)
        }

    val createPost: (
        title: String,
        preview: String,
        content: String,
        hashtagId: Int,
        eventDate: ZonedDateTime?,
        authorId: Int,
        moderatorId: Int?,
        status: Status,
    ) -> Result4k<Post, PostCreationError> =
        CreatePost(
            insertPost = {
                    title,
                    preview,
                    content,
                    hashtagId,
                    eventDate,
                    authorId,
                    moderatorId,
                    status,
                ->
                postsDatabase.insertPost(
                    title = title,
                    preview = preview,
                    content = content,
                    hashtagId = hashtagId,
                    eventDate = eventDate,
                    authorId = authorId,
                    moderatorId = moderatorId,
                    status = status
                )
            },
            selectHashtagById = hashtagsDatabase::selectHashtagByID,
            selectUserById = userDatabase::selectUserByID,
            selectMediaByName = mediaDatabase::selectOnlyMeta
        )

    val changeTitle: (Post, String) -> Result4k<Post, FieldInPostChangingError> =
        ChangeStringFieldInPost(
            maxLength = Post.MAX_TITLE_LENGTH,
            pattern = Regex(".*"),
            changeField = postsDatabase::updateTitle
        )

    val changePreview: (Post, String) -> Result4k<Post, FieldInPostChangingError> =
        ChangePreviewInPost(
            changePreview = postsDatabase::updatePreview,
            selectMediaByName = mediaDatabase::selectOnlyMeta
        )

    val changeContent: (Post, String) -> Result4k<Post, FieldInPostChangingError> =
        ChangeStringFieldInPost(
            maxLength = 0,
            pattern = Regex(".*"),
            changeField = postsDatabase::updateContent
        )

    val changeHashtagId: (Post, Int) -> Result4k<Post, FieldInPostChangingError> =
        ChangeHashtagIdInPost(
            changeHashtagId = postsDatabase::updateHashtagId,
            selectHashtagById = hashtagsDatabase::selectHashtagByID
        )

    val changeEventDate: (Post, ZonedDateTime) -> Result4k<Post, FieldInPostChangingError> =
        ChangeDateFieldInPost(
            changeField = postsDatabase::updateEventDate
        )

    val changeAuthorId: (Post, Int) -> Result4k<Post, FieldInPostChangingError> =
        ChangeUserIdInPost(
            changeUserId = postsDatabase::updateAuthorId,
            selectUserById = userDatabase::selectUserByID
        )

    val changeModeratorId: (Post, Int) -> Result4k<Post, FieldInPostChangingError> =
        ChangeUserIdInPost(
            changeUserId = postsDatabase::updateAuthorId,
            selectUserById = userDatabase::selectUserByID
        )

    val makePublished: (Post) -> Result4k<Post, MakeStatusError> =
        StatusChanger(
            targetStatus = Status.PUBLISHED,
            alreadyHasStatusError = MakeStatusError.IS_ALREADY_PUBLISHED,
            updateStatus = postsDatabase::updateStatus,
            unknownError = MakeStatusError.UNKNOWN_DATABASE_ERROR
        )

    val makeHidden: (Post) -> Result4k<Post, MakeStatusError> =
        StatusChanger(
            targetStatus = Status.HIDDEN,
            alreadyHasStatusError = MakeStatusError.IS_ALREADY_HIDDEN,
            updateStatus = postsDatabase::updateStatus,
            unknownError = MakeStatusError.UNKNOWN_DATABASE_ERROR
        )

    val makeModeration: (Post) -> Result4k<Post, MakeStatusError> =
        StatusChanger(
            targetStatus = Status.MODERATION,
            alreadyHasStatusError = MakeStatusError.IS_ALREADY_MODERATION,
            updateStatus = postsDatabase::updateStatus,
            unknownError = MakeStatusError.UNKNOWN_DATABASE_ERROR
        )

    val makeDraft: (Post) -> Result4k<Post, MakeStatusError> =
        StatusChanger(
            targetStatus = Status.DRAFT,
            alreadyHasStatusError = MakeStatusError.IS_ALREADY_DRAFT,
            updateStatus = postsDatabase::updateStatus,
            unknownError = MakeStatusError.UNKNOWN_DATABASE_ERROR
        )

    val changePost: (Post, String, String, String, Int, ZonedDateTime?, Int, Int?)
    -> Result4k<Post, FieldInPostChangingError> =
        ChangePost(
            changePost = postsDatabase::updatePost,
            selectHashtagById = hashtagsDatabase::selectHashtagByID,
            selectMediaByName = mediaDatabase::selectOnlyMeta,
            selectUserById = userDatabase::selectUserByID
        )
}
