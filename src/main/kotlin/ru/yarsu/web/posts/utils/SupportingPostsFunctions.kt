@file:Suppress("detekt:TooManyFunctions")

package ru.yarsu.web.posts.utils

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import org.http4k.lens.WebForm
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.hashtags.HashtagCreationError
import ru.yarsu.domain.operations.hashtags.HashtagFetchingError
import ru.yarsu.domain.operations.hashtags.HashtagOperationsHolder
import ru.yarsu.domain.operations.media.MediaOperationsHolder
import ru.yarsu.domain.operations.posts.FieldInPostChangingError
import ru.yarsu.domain.operations.posts.PostCreationError
import ru.yarsu.domain.operations.posts.PostFetchingError
import ru.yarsu.domain.operations.posts.PostOperationsHolder
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.form.addFailure
import ru.yarsu.web.form.toCustomForm
import ru.yarsu.web.lenses.GeneralWebLenses.from
import ru.yarsu.web.posts.POST_SEGMENT
import ru.yarsu.web.posts.lenses.PostLensErrors
import ru.yarsu.web.posts.lenses.PostWebLenses.contentField
import ru.yarsu.web.posts.lenses.PostWebLenses.eventDateField
import ru.yarsu.web.posts.lenses.PostWebLenses.hashtagField
import ru.yarsu.web.posts.lenses.PostWebLenses.hashtagInputField
import ru.yarsu.web.posts.lenses.PostWebLenses.previewField
import ru.yarsu.web.posts.lenses.PostWebLenses.titleField
import ru.yarsu.web.posts.models.NewPostVM
import ru.yarsu.web.redirect
import java.time.ZonedDateTime

@Suppress("ReturnCount")
fun WebForm.validateForm(
    user: User,
    hashtagOperations: HashtagOperationsHolder,
    postId: Int?,
): Result<Pair<Post, String>, Pair<String, String>> {
    val title = titleField from this
    val preview = previewField from this
    val content = contentField from this
    val hashtag = hashtagField from this
    val newHashtag = hashtagInputField from this
    val eventDate = eventDateField from this
    val creationDate = ZonedDateTime.now()
    var hashtagId = -1
    val hashtagTitle: String
    when (hashtag) {
        -1 -> newHashtag?.let {
            when (fetchHashtagByTitle(newHashtag, hashtagOperations)) {
                is Failure -> hashtagTitle = newHashtag

                is Success -> return Failure("hashtag" to CreationHashtagError.HASHTAG_ALREADY_EXISTS.errorText)
            }
        } ?: return Failure("hashtag" to PostLensErrors.HASHTAG_INPUT_NOT_CORRECT.errorText)

        else -> when (
            val fetchedHashtag = fetchHashtagById(
                id = hashtag,
                hashtagOperations = hashtagOperations
            )
        ) {
            is Failure -> return Failure("hashtag" to fetchedHashtag.reason.errorText)
            is Success -> {
                hashtagId = fetchedHashtag.value.id
                hashtagTitle = fetchedHashtag.value.title
            }
        }
    }

    return Success(
        Pair(
            Post(
                id = postId ?: -1,
                title = title,
                preview = preview,
                content = content,
                hashtagId = hashtagId,
                eventDate = eventDate,
                creationDate = creationDate,
                lastModifiedDate = creationDate,
                authorId = user.id,
                moderatorId = null,
                status = Status.DRAFT
            ),
            hashtagTitle
        )
    )
}

fun Request.responseWithError(
    name: String = "no-specific",
    description: String,
    render: ContextAwareViewRender,
    hashtagOperations: HashtagOperationsHolder,
    form: WebForm,
): Response =
    render(this) extract
        NewPostVM(
            form = form.toCustomForm().addFailure(name, description),
            hashTags = hashtagOperations
                .fetchAllHashtags()
                .valueOrNull()
                ?: emptyList()
        )

fun fetchHashtagByTitle(
    title: String,
    hashtagOperations: HashtagOperationsHolder,
): Result<Hashtag, FetchingHashtagError> {
    return when (
        val hashtag = hashtagOperations.fetchHashtagByTitle(title)
    ) {
        is Failure -> when (hashtag.reason) {
            HashtagFetchingError.NO_SUCH_HASHTAG -> Failure(FetchingHashtagError.NO_SUCH_HASHTAG)
            HashtagFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(FetchingHashtagError.UNKNOWN_DATABASE_ERROR)
        }

        is Success -> Success(hashtag.value)
    }
}

fun fetchHashtagById(
    id: Int,
    hashtagOperations: HashtagOperationsHolder,
): Result<Hashtag, FetchingHashtagError> {
    return when (
        val hashtag = hashtagOperations.fetchHashtagById(id)
    ) {
        is Failure -> when (hashtag.reason) {
            HashtagFetchingError.NO_SUCH_HASHTAG -> Failure(FetchingHashtagError.NO_SUCH_HASHTAG)
            HashtagFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(FetchingHashtagError.UNKNOWN_DATABASE_ERROR)
        }

        is Success -> Success(hashtag.value)
    }
}

fun createHashtag(
    title: String,
    hashtagOperations: HashtagOperationsHolder,
): Result<Hashtag, CreationHashtagError> {
    return when (
        val hashtag = hashtagOperations.createHashtag(title)
    ) {
        is Failure -> when (hashtag.reason) {
            HashtagCreationError.HASHTAG_ALREADY_EXISTS -> Failure(CreationHashtagError.HASHTAG_ALREADY_EXISTS)
            HashtagCreationError.INVALID_HASHTAG_DATA -> Failure(CreationHashtagError.INVALID_HASHTAG_DATA)
            HashtagCreationError.UNKNOWN_DATABASE_ERROR -> Failure(CreationHashtagError.UNKNOWN_DATABASE_ERROR)
        }

        is Success -> Success(hashtag.value)
    }
}

fun addNewPost(
    post: Post,
    postOperations: PostOperationsHolder,
): Result<Post, PostError> {
    return when (
        val newPost = postOperations.createPost(
            post.title,
            post.preview,
            post.content,
            post.hashtagId,
            post.eventDate,
            post.authorId,
            post.moderatorId,
            post.status
        )
    ) {
        is Failure -> when (newPost.reason) {
            PostCreationError.INVALID_POST_DATA -> Failure(PostError.INVALID_POST_DATA)
            PostCreationError.UNKNOWN_DATABASE_ERROR -> Failure(PostError.UNKNOWN_DATABASE_ERROR)
            PostCreationError.MEDIA_NOT_EXISTS -> Failure(PostError.MEDIA_NOT_EXISTS)
            PostCreationError.HASHTAG_NOT_EXISTS -> Failure(PostError.HASHTAG_NOT_EXISTS)
            PostCreationError.AUTHOR_NOT_EXISTS -> Failure(PostError.AUTHOR_NOT_EXISTS)
            PostCreationError.MODERATOR_NOT_EXISTS -> Failure(PostError.MODERATOR_NOT_EXISTS)
        }

        is Success -> Success(newPost.value)
    }
}

fun updateExistingPost(
    post: Post,
    postOperations: PostOperationsHolder,
    user: User,
): Result<Post, PostError> =
    when {
        user canEdit post -> when (
            val newPost = postOperations.changePost(
                post,
                post.title,
                post.preview,
                post.content,
                post.hashtagId,
                post.eventDate,
                post.authorId,
                post.moderatorId,
            )
        ) {
            is Failure -> when (newPost.reason) {
                FieldInPostChangingError.TITLE_IS_BLANK_OR_EMPTY -> Failure(PostError.INVALID_POST_DATA)
                FieldInPostChangingError.UNKNOWN_DATABASE_ERROR -> Failure(PostError.UNKNOWN_DATABASE_ERROR)
                FieldInPostChangingError.MEDIA_NOT_EXISTS -> Failure(PostError.MEDIA_NOT_EXISTS)
                FieldInPostChangingError.HASHTAG_NOT_EXISTS -> Failure(PostError.HASHTAG_NOT_EXISTS)
                FieldInPostChangingError.USER_NOT_EXISTS -> Failure(PostError.AUTHOR_NOT_EXISTS)
                else -> Failure(PostError.UNKNOWN_DATABASE_ERROR)
            }

            is Success -> Success(newPost.value)
        }

        else -> Failure(PostError.INVALID_POST_DATA)
    }

fun fetchPostById(
    postId: Int,
    postOperations: PostOperationsHolder,
): Result<Post, FetchingPostError> {
    return when (
        val post = postOperations.fetchPostById(postId)
    ) {
        is Failure -> when (post.reason) {
            PostFetchingError.NO_SUCH_POST -> Failure(FetchingPostError.NO_SUCH_POST)
            PostFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(FetchingPostError.UNKNOWN_DATABASE_ERROR)
        }

        is Success -> Success(post.value)
    }
}

@Suppress("LongParameterList")
fun Request.trySavePostAndHashtag(
    postAndHashtag: Pair<Post, String>,
    postOperations: PostOperationsHolder,
    mediaOperations: MediaOperationsHolder,
    hashtagOperations: HashtagOperationsHolder,
    render: ContextAwareViewRender,
    form: WebForm,
    user: User,
): Response {
    var postHashtagId = postAndHashtag.first.hashtagId
    return when (val mediaMarkingResult = updateMediaMarks(postAndHashtag.first, mediaOperations)) {
        is Success -> {
            if (postHashtagId == -1) {
                when (val newHashtag = createHashtag(postAndHashtag.second, hashtagOperations)) {
                    is Failure -> this.responseWithError(
                        description = newHashtag.reason.errorText,
                        render = render,
                        hashtagOperations = hashtagOperations,
                        form = form,
                    )

                    is Success -> {
                        postHashtagId = newHashtag.value.id
                    }
                }
            }
            when (
                val newPost = when (postAndHashtag.first.id) {
                    -1 -> addNewPost(postAndHashtag.first.copy(hashtagId = postHashtagId), postOperations)
                    else -> updateExistingPost(
                        post = postAndHashtag.first.copy(hashtagId = postHashtagId),
                        postOperations = postOperations,
                        user = user,
                    )
                }
            ) {
                is Failure -> this.responseWithError(
                    description = newPost.reason.errorText,
                    render = render,
                    hashtagOperations = hashtagOperations,
                    form = form,
                )

                is Success -> redirect("${POST_SEGMENT}/${newPost.value.id}")
            }
        }

        is Failure -> this.responseWithError(
            description = mediaMarkingResult.reason,
            render = render,
            hashtagOperations = hashtagOperations,
            form = form,
        )
    }
}

@Suppress("detekt:ReturnCount")
private fun updateMediaMarks(
    post: Post,
    mediaOperations: MediaOperationsHolder,
): Result<Boolean, String> {
    MediaType
        .entries
        .minus(MediaType.VIDEO)
        .flatMap { mediaType -> // get all media filenames from task (without video)
            mediaType
                .pattern
                .findAll(post.content)
                .toList()
                .map { it.groupValues[1] }
        }.map { // fetch media from db
            mediaOperations
                .fetchOnlyMeta(it)
                .valueOrNull()
                ?: return Failure(PostError.UNKNOWN_DATABASE_ERROR.errorText)
        }.filter { it.isTemporary } // filter not temporary media
        .map { // make regular only temporary media
            mediaOperations.makeMediaRegular(it).valueOrNull()
                ?: return Failure(PostError.UNKNOWN_DATABASE_ERROR.errorText)
        }
    return Success(true)
}

infix fun User.canEdit(post: Post): Boolean = this.hasModeratorPermissions(post) || this.hasWriterPermissions(post)

private fun User.hasModeratorPermissions(post: Post): Boolean =
    this.role == Role.MODERATOR && (post.status == Status.MODERATION || post.status == Status.DRAFT)

private fun User.hasWriterPermissions(post: Post): Boolean =
    this.role == Role.WRITER && post.authorId == this.id && post.status == Status.DRAFT

enum class FetchingHashtagError(val errorText: String) {
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
    NO_SUCH_HASHTAG("Указанный хэштег не найден"),
}

enum class CreationHashtagError(val errorText: String) {
    HASHTAG_ALREADY_EXISTS("Введённый хэштэг уже сушествует. Пожалуйста, выберите его из списка хэштегов"),
    INVALID_HASHTAG_DATA("Неверный формат введённого хэштега"),
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
}

enum class PostError(val errorText: String) {
    HASHTAG_NOT_EXISTS("Указанный хэштег не существует"),
    AUTHOR_NOT_EXISTS("Писатель не существует"),
    MEDIA_NOT_EXISTS("Указанное медиа не существует"),
    MODERATOR_NOT_EXISTS("Указанный модератор не существует"),
    INVALID_POST_DATA("Неверные данные поста"),
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
}

enum class FetchingPostError(val errorText: String) {
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
    NO_SUCH_POST("Указанного поста не существует"),
}
