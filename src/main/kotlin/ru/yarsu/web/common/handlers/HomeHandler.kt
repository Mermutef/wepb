package ru.yarsu.web.common.handlers

import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.User
import ru.yarsu.web.common.models.HomeVM
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import kotlin.contracts.contract

class HomeHandler(
    private val render: ContextAwareViewRender,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        println(userLens(request))
        return render(request) extract HomeVM(userLens(request))
    }
}
