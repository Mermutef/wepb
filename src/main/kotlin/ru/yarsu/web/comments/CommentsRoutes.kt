package ru.yarsu.web.comments

import org.http4k.core.Method
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.comments.handlers.CommentHandler
import ru.yarsu.web.comments.handlers.GiveAllCommentsHandler
import ru.yarsu.web.comments.handlers.HideCommentHandler
import ru.yarsu.web.comments.handlers.NewCommentHandler
import ru.yarsu.web.comments.handlers.UpdateCommentHandler
import ru.yarsu.web.context.ContextTools

fun commentsRoutes(
    operations: OperationsHolder,
    contextTools: ContextTools,
) = routes(
    "/{id}/all" bind Method.GET to GiveAllCommentsHandler(operations),
    "/{id}/new" bind Method.POST to NewCommentHandler(operations, contextTools.userLens),
    "/{id}/delete" bind Method.POST to HideCommentHandler(operations, contextTools.userLens),
    "/{id}/edit" bind Method.POST to UpdateCommentHandler(operations, contextTools.userLens),
    "/{id}/" bind Method.GET to CommentHandler(operations),
)

const val COMMENTS_SEGMENT = "/comments"
