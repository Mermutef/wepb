package ru.yarsu.web.comments.handlers

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.lenses.GeneralWebLenses.idOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.ok

private val mapper = jacksonObjectMapper().let { it.apply { it.registerModule(JavaTimeModule()) } }

class GiveAllCommentsHandler(
    private val operations: OperationsHolder,
) : HttpHandler {
    override fun invoke(request: Request): Response =
        request.idOrNull()?.let { postId ->
            when (val comments = operations.commentOperations.fetchPublishedCommentsInPost(postId)) {
                is Success -> {
                    val commentsWithAuthors = comments.value.map { comment ->
                        val user = operations
                            .userOperations
                            .fetchUserByID(comment.authorId)
                            .valueOrNull()
                            ?: return notFound
                        mapOf(
                            "comment" to comment,
                            "author" to user,
                        )
                    }
                    ok(mapper.writeValueAsString(commentsWithAuthors))
                }

                is Failure -> notFound
            }
        } ?: notFound
}
