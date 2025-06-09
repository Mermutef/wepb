package ru.yarsu.web.comments

import org.http4k.core.Method
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.comments.handlers.GiveAllCommentsHandler
import ru.yarsu.web.comments.handlers.SaveCommentHandler
import ru.yarsu.web.context.ContextTools

fun commentsRoutes(operations: OperationsHolder, contextTools: ContextTools,) = routes(
    "/{id}$ALL_COMMENTS" bind Method.GET to GiveAllCommentsHandler(operations),
    "/{id}$NEW_COMMENT" bind Method.POST to SaveCommentHandler(operations, contextTools.userLens),
)

val COMMENTS_SEGMENT = "/comments"
val ALL_COMMENTS = "/all"
val NEW_COMMENT = "/new"