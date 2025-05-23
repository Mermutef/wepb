package ru.yarsu.web.auth.handlers

import org.http4k.core.*
import ru.yarsu.web.auth.models.SignInVM
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract

class ShowSignInFormHandler(
    private val render: ContextAwareViewRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return render(request) extract SignInVM()
    }
}
