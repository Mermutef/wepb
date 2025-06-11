package ru.yarsu.web.comments.handlers

import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.lenses.GeneralWebLenses.idOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.ok

class HideCommentHandler(
    private val operations: OperationsHolder,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    @Suppress("detekt:ReturnCount")
    override fun invoke(request: Request): Response {
        val commentId = request.idOrNull() ?: return notFound
        val user = userLens(request) ?: return notFound
        val comment = operations.commentOperations.fetchCommentById(commentId).valueOrNull()
            ?: return notFound

        return when {
            user.isModerator() || user.id == comment.authorId ->
                operations
                    .commentOperations
                    .makeHidden(comment)
                    .valueOrNull()
                    ?.let { ok() }
                    ?: notFound

            else -> notFound
        }
    }
}
