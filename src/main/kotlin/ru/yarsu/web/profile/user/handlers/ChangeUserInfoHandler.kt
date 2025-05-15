package ru.yarsu.web.profile.user.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.lens.RequestContextLens
import ru.yarsu.config.AuthConfig
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.auth.lenses.UserWebLenses.authorizeUserFromPath
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.lenses.GeneralWebLenses.lensOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.profile.user.lenses.UserFieldsWebLenses.userFieldNameFromPath
import ru.yarsu.web.profile.user.utils.changeField

class ChangeUserInfoHandler(
    private val render: ContextAwareViewRender,
    private val operations: OperationsHolder,
    private val config: AuthConfig,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response =
        lensOrNull(userFieldNameFromPath, request)?.let {
            when (
                val user = request.authorizeUserFromPath(
                    userLens = userLens,
                )
            ) {
                is Failure -> notFound
                is Success -> operations.changeField(
                    request = request,
                    render = render,
                    user = user.value,
                    field = it,
                    config = config,
                )
            }
        } ?: notFound
}
