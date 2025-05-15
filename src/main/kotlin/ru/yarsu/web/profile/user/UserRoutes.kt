package ru.yarsu.web.profile.user

import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.profile.user.handlers.ChangeUserInfoHandler
import ru.yarsu.web.profile.user.handlers.UserHandler

fun userRoutes(
    contextTools: ContextTools,
    operations: OperationsHolder,
    config: AppConfig,
) = routes(
    "/{login}/{field}" bind POST to ChangeUserInfoHandler(
        render = contextTools.render,
        operations = operations,
        config = config.authConfig,
        userLens = contextTools.userLens,
    ),
    "/{login}" bind GET to UserHandler(
        contextTools.render,
        contextTools.userLens,
    ),
)

const val USER = "/user"
