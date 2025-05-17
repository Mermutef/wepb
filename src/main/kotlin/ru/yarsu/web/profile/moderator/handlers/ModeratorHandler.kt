package ru.yarsu.web.profile.moderator.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.hashtags.HashtagsOperationsHolder
import ru.yarsu.domain.operations.posts.PostFetchingError
import ru.yarsu.domain.operations.posts.PostsOperationsHolder
import ru.yarsu.web.auth.lenses.UserWebLenses.authorizeUserFromPath
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.notFound
import ru.yarsu.web.profile.moderator.models.ModeratorRoomVM
import ru.yarsu.web.profile.writer.handlers.FetchingPostError

class ModeratorHandler(
    private val render: ContextAwareViewRender,
    private val postsOperations: PostsOperationsHolder,
    private val hashtagsOperations: HashtagsOperationsHolder,
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
                if (user.value.role != Role.MODERATOR) {
                    notFound
                } else {
                    when (val writerPosts = fetchAllPosts()) {
                        is Failure -> notFound
                        is Success -> render(request) extract ModeratorRoomVM(
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
