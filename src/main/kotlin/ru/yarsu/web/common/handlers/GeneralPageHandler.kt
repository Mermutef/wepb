package ru.yarsu.web.common.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.operations.hashtags.HashtagsOperationsHolder
import ru.yarsu.domain.operations.posts.PostFetchingError
import ru.yarsu.domain.operations.posts.PostsOperationsHolder
import ru.yarsu.web.common.models.GeneralPageVM
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.notFound
import ru.yarsu.web.profile.writer.handlers.FetchingPostError

class GeneralPageHandler(
    private val render: ContextAwareViewRender,
    private val postsOperations: PostsOperationsHolder,
    private val hashtagsOperations: HashtagsOperationsHolder,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return when (val publishedPosts = fetchPublishedPosts()) {
            is Failure -> notFound
            is Success -> render(request) extract GeneralPageVM(
                publishedPosts.value.associateWith {
                    hashtagsOperations.fetchHashtagById(
                        it.hashtagId
                    ).valueOrNull() ?: Hashtag.nullTag
                }
            )
        }
    }

    private fun fetchPublishedPosts(): Result<List<Post>, FetchingPostError> {
        return when (
            val posts = postsOperations.fetchPostsByStatus(Status.PUBLISHED)
        ) {
            is Failure -> when (posts.reason) {
                PostFetchingError.NO_SUCH_POST -> Failure(FetchingPostError.NO_SUCH_POST)
                PostFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(FetchingPostError.UNKNOWN_DATABASE_ERROR)
            }

            is Success -> Success(posts.value)
        }
    }
}
