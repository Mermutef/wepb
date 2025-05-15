package ru.yarsu.web.profile.user.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.User
import ru.yarsu.web.auth.lenses.UserWebLenses.authorizeUserFromPath
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.notFound
import ru.yarsu.web.profile.user.models.UserRoomVM
import ru.yarsu.web.profile.user.utils.defaultForm

class UserHandler(
    private val render: ContextAwareViewRender,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response =
        when (val user = request.authorizeUserFromPath(userLens = userLens)) {
            is Failure -> notFound
            is Success -> render(request) extract UserRoomVM(user = user.value, form = user.value.defaultForm())
        }
}
