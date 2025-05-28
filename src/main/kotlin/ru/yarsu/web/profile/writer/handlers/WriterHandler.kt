package ru.yarsu.web.profile.writer.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.hashtags.HashtagOperationsHolder
import ru.yarsu.domain.operations.posts.PostFetchingError
import ru.yarsu.domain.operations.posts.PostOperationsHolder
import ru.yarsu.web.auth.lenses.UserWebLenses.authorizeUserFromPath
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.notFound
import ru.yarsu.web.profile.writer.models.WriterRoomVM

class WriterHandler(
    private val render: ContextAwareViewRender,
    private val postsOperations: PostOperationsHolder,
    private val hashtagsOperations: HashtagOperationsHolder,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return when (
            val user = request.authorizeUserFromPath(
                userLens = userLens
            )
        ) {
            is Failure -> notFound
            is Success -> {
                val writer = user.value
                when (val writerPosts = fetchWriterPosts(writer)) {
                    is Failure -> notFound
                    is Success -> render(request) extract WriterRoomVM(
                        writerPosts.value.associateWith {
                            hashtagsOperations.fetchHashtagById(
                                it.hashtagId
                            ).valueOrNull() ?: Hashtag.nullTag
                        }
                    )
                }
            }
        }
    }

    private fun fetchWriterPosts(writer: User): Result<List<Post>, FetchingPostError> {
        return when (
            val posts = postsOperations.fetchPostsByAuthorId(writer.id)
        ) {
            is Failure -> when (posts.reason) {
                PostFetchingError.NO_SUCH_POST -> Failure(FetchingPostError.NO_SUCH_POST)
                PostFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(FetchingPostError.UNKNOWN_DATABASE_ERROR)
            }

            is Success -> Success(posts.value)
        }
    }
}

enum class FetchingPostError(val errorText: String) {
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
    NO_SUCH_POST("Указанные посты не найдены"),
}

enum class CreationPostError(val errorText: String) {
    HASHTAG_NOT_EXISTS("Хэжтэга не существует"),
    INVALID_POST_DATA("Неверные данные поста"),
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
}
