package ru.yarsu.web.comments.handlers

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
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

class SaveCommentHandler(
    private val operations: OperationsHolder,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val postId = request.idOrNull() ?: return notFound
        val user = userLens(request) ?: return notFound
        val content = mapper.readValue<Map<String, String>>(request.bodyString())["content"]?.takeIf { it.isNotBlank() }
            ?: return ok()
        return operations.commentOperations.createComment(content, user.id, postId).valueOrNull()
            ?.let { ok() } ?: internalServerError
    }
}