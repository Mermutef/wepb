package ru.yarsu.web.profile.moderator

import org.http4k.core.*
import org.http4k.routing.*
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.profile.moderator.handlers.ModeratorHandler

fun moderatorRoutes(
    contextTools: ContextTools,
    operations: OperationsHolder,
) = routes(
    "$POST_MODERATION/{login}" bind Method.GET to ModeratorHandler(
        contextTools.render,
        operations.postOperations,
        operations.hashtagOperations,
        contextTools.userLens
    ),
)

const val MODERATOR_SEGMENT = "/moderator"
const val POST_MODERATION = "/post-moderation"
