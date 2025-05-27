package ru.yarsu.web.profile.moderator.handlers

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
import ru.yarsu.web.profile.moderator.models.ModeratorRoomVM
import ru.yarsu.web.profile.writer.handlers.FetchingPostError

class ModeratorHandler(
    private val render: ContextAwareViewRender,
    private val postsOperations: PostOperationsHolder,
    private val hashtagsOperations: HashtagOperationsHolder,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return when (
            request.authorizeUserFromPath(
                userLens = userLens
            )
        ) {
            is Failure -> notFound
            is Success -> {
                when (val posts = fetchAllPosts()) {
                    is Failure -> notFound
                    is Success -> render(request) extract ModeratorRoomVM(
                        posts.value.associateWith {
                            hashtagsOperations.fetchHashtagById(
                                it.hashtagId
                            ).valueOrNull() ?: Hashtag.nullTag
                        }
                    )
                }
            }
        }
    }

    private fun fetchAllPosts(): Result<List<Post>, FetchingPostError> {
        return when (
            val posts = postsOperations.fetchAllPosts()
        ) {
            is Failure -> when (posts.reason) {
                PostFetchingError.NO_SUCH_POST -> Failure(FetchingPostError.NO_SUCH_POST)
                PostFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(FetchingPostError.UNKNOWN_DATABASE_ERROR)
            }

            is Success -> Success(posts.value)
        }
    }
}
