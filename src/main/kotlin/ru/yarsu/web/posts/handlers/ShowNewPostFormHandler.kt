package ru.yarsu.web.posts.handlers

import org.http4k.core.*
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.posts.models.NewPostVM

class ShowNewPostFormHandler(
    private val render: ContextAwareViewRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return render(request) extract NewPostVM()
    }
}
