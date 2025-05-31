package ru.yarsu.web.posts.handlers

import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import ru.yarsu.domain.operations.hashtags.HashtagOperationsHolder
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.posts.models.NewPostVM

class ShowNewPostFormHandler(
    private val render: ContextAwareViewRender,
    private val hashtagsOperations: HashtagOperationsHolder,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return render(request) extract NewPostVM(
            hashTags = hashtagsOperations
                .fetchAllHashtags()
                .valueOrNull()
                ?: emptyList()
        )
    }
}
