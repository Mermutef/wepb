// todo удалить Suppress после наполнения файла контентом
@file:Suppress("ktlint:standard:no-empty-file")

package ru.yarsu.web.profile.user

import org.http4k.core.Method.GET
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.profile.user.handlers.UserHandler

fun userRoutes(
    contextTools: ContextTools,
    operations: OperationsHolder,
) = routes(
    "/{login}" bind GET to UserHandler(
        contextTools.render,
//        operations,
        contextTools.userLens,
    ),
)

const val USER = "/user"