package ru.yarsu.domain.operations.posts

import dev.forkhandles.result4k.Result4k
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.dependencies.HashtagsDatabase
import ru.yarsu.domain.dependencies.PostsDatabase
import ru.yarsu.domain.models.Post
import java.time.ZonedDateTime

class PostsOperationsHolder(
    private val postsDatabase: PostsDatabase,
    private val hashtagsDatabase: HashtagsDatabase,
) {

    val fetchAllPosts: () -> Result4k<List<Post>, PostFetchingError> = FetchAllPosts { postsDatabase.selectAllPosts() }

    val fetchPostById: (Int) -> Result4k<Post, PostFetchingError> =
        FetchPostById { postID: Int -> postsDatabase.selectPostByID(postID) }

    val fetchPostsByHashtagId: (Int) -> Result4k<List<Post>, PostFetchingError> =
        FetchPostByIdHashtag { hashtagId: Int -> postsDatabase.selectPostsByIdHashtag(hashtagId) }

    val fetchNNewPosts: (Int) -> Result4k<List<Post>, PostFetchingError> =
        FetchNNewPosts { countN: Int -> postsDatabase.selectNNewPosts(countN) }

    val fetchPostsByAuthorId: (Int) -> Result4k<List<Post>, PostFetchingError> =
        FetchPostsByAuthorId { authorId: Int -> postsDatabase.selectPostsByAuthorId(authorId) }

    val fetchPostsByModeratorId: (Int) -> Result4k<List<Post>, PostFetchingError> =
        FetchPostsByModeratorId { moderatorId: Int -> postsDatabase.selectPostsByModeratorId(moderatorId) }

    val fetchPostsByStatus: (Status) -> Result4k<List<Post>, PostFetchingError> =
        FetchPostByStatus { status: Status -> postsDatabase.selectPostByStatus(status) }

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
        CreatePosts(
            insertPost = {
                    title,
                    preview,
                    content,
                    hashtagId,
                    eventDate,
                    _,
                    _,
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
                    creationDate = ZonedDateTime.now(),
                    lastModifiedDate = ZonedDateTime.now(),
                    authorId = authorId,
                    moderatorId = moderatorId,
                    status = status
                )
            },
            selectHashtagById = hashtagsDatabase::selectHashtagByID
        )

    val changeTitle: (Post, String) -> Result4k<Post, FieldInPostChangingError> =
        ChangeStringFieldInPost(
            maxLength = Post.MAX_TITLE_LENGTH,
            pattern = Post.titlePattern,
            changeField = postsDatabase::updateTitle
        )

    val changePreview: (Post, String) -> Result4k<Post, FieldInPostChangingError> =
        ChangeStringFieldInPost(
            maxLength = Post.MAX_PREVIEW_LENGTH,
            pattern = Regex(""),
            changeField = postsDatabase::updatePreview
        )

    val changeContent: (Post, String) -> Result4k<Post, FieldInPostChangingError> =
        ChangeStringFieldInPost(
            maxLength = 0,
            pattern = Regex(""),
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
        ChangeIntFieldInPost(
            changeField = postsDatabase::updateAuthorId
        )

    val changeModeratorId: (Post, Int) -> Result4k<Post, FieldInPostChangingError> =
        ChangeIntFieldInPost(
            changeField = postsDatabase::updateModeratorId
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
}
