package ru.yarsu.web.comments.handlers

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.lenses.GeneralWebLenses.idOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.ok

private val mapper = jacksonObjectMapper().let { it.apply { it.registerModule(JavaTimeModule()) } }

class CommentHandler(
    private val operations: OperationsHolder,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val commentId = request.idOrNull() ?: return notFound
        return when (val comment = operations.commentOperations.fetchCommentById(commentId)) {
            is Success -> ok(mapper.writeValueAsString(comment.value))

            is Failure -> notFound
        }
    }
}
