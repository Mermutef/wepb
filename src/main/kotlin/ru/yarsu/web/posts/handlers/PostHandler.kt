package ru.yarsu.web.posts.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.core.*
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.hashtags.HashtagOperationsHolder
import ru.yarsu.domain.operations.posts.PostOperationsHolder
import ru.yarsu.domain.operations.users.UserFetchingError
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.lenses.GeneralWebLenses.idOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.posts.models.PostVM
import ru.yarsu.web.posts.utils.fetchHashtagById
import ru.yarsu.web.posts.utils.fetchPostById

class PostHandler(
    private val postsOperations: PostOperationsHolder,
    private val hashtagOperations: HashtagOperationsHolder,
    private val userOperations: UserOperationsHolder,
    private val render: ContextAwareViewRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val postId = request.idOrNull() ?: return notFound
        return when (val post = fetchPostById(postId, postsOperations)) {
            is Failure -> notFound
            is Success -> {
                when (val hashtag = fetchHashtagById(post.value.hashtagId, hashtagOperations)) {
                    is Failure -> notFound
                    is Success -> {
                        when (
                            val postAuthor = fetchUserById(
                                id = post.value.authorId,
                                userOperations = userOperations
                            )
                        ) {
                            is Failure -> notFound
                            is Success -> render(request) extract PostVM(
                                postWithHashtag = Pair(post.value, hashtag.value),
                                author = postAuthor.value
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun fetchUserById(
    id: Int,
    userOperations: UserOperationsHolder,
): Result<User, FetchingUserError> {
    return when (
        val user = userOperations.fetchUserByID(id)
    ) {
        is Failure -> when (user.reason) {
            UserFetchingError.NO_SUCH_USER -> Failure(FetchingUserError.NO_SUCH_USER)
            UserFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(FetchingUserError.UNKNOWN_DATABASE_ERROR)
        }

        is Success -> Success(user.value)
    }
}

enum class FetchingUserError(val errorText: String) {
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
    NO_SUCH_USER("Указанного пользователя не существует"),
}
