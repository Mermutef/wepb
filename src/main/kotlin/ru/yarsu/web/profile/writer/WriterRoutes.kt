package ru.yarsu.web.profile.writer

import org.http4k.core.*
import org.http4k.routing.*
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.profile.writer.handlers.WriterHandler

fun writerRoutes(
    contextTools: ContextTools,
    operations: OperationsHolder,
) = routes(
    "$MY_POSTS/{login}" bind Method.GET to WriterHandler(
        contextTools.render,
        operations.postOperations,
        operations.hashtagOperations,
        contextTools.userLens
    ),
)

const val WRITER_SEGMENT = "/writer"
const val MY_POSTS = "/my-posts"
