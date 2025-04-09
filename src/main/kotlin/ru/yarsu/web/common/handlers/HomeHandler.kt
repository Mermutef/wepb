package ru.yarsu.web.common.handlers

import org.http4k.core.*
import ru.yarsu.web.common.models.HomeVM
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract

class HomeHandler(
    private val render: ContextAwareViewRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return render(request) extract HomeVM()
    }
}
