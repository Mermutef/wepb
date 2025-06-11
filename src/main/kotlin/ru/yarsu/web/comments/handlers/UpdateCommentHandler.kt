package ru.yarsu.web.comments.handlers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.internalServerError
import ru.yarsu.web.lenses.GeneralWebLenses.idOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.ok

private val mapper = jacksonObjectMapper()

class UpdateCommentHandler(
    private val operations: OperationsHolder,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    @Suppress("detekt:ReturnCount")
    override fun invoke(request: Request): Response {
        val commentId = request.idOrNull() ?: return notFound
        val user = userLens(request) ?: return notFound
        val content = mapper.readValue<Map<String, String>>(request.bodyString())["content"]?.takeIf { it.isNotBlank() }
            ?: return notFound
        val comment =
            operations.commentOperations.fetchCommentById(commentId).valueOrNull()?.takeIf { it.authorId == user.id }
                ?: return notFound
        return operations.commentOperations.changeContent(comment, content).valueOrNull()
            ?.let { ok() } ?: internalServerError
    }
}
