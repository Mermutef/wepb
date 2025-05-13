package ru.yarsu.web.profile

import org.http4k.core.*
import org.http4k.routing.*
import ru.yarsu.web.context.ContextTools

fun profileRoutes(contextTools: ContextTools): RoutingHttpHandler =
    routes(
        "/{login}" bind Method.GET to RedirectToHandlers(
            userLens = contextTools.userLens
        ),
    )

const val PROFILE_SEGMENT = "/profile"
