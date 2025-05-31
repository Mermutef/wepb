package ru.yarsu.web.posts

import org.http4k.core.*
import org.http4k.routing.*
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.posts.handlers.SavePostHandler
import ru.yarsu.web.posts.handlers.ShowNewPostFormHandler

fun postsRoutes(
    contextTools: ContextTools,
    operations: OperationsHolder,
) = routes(
    CREATE_POST bind Method.GET to ShowNewPostFormHandler(
        render = contextTools.render,
        hashtagsOperations = operations.hashtagOperations
    ),
    CREATE_POST bind Method.POST to SavePostHandler(
        postOperations = operations.postOperations,
        hashtagOperations = operations.hashtagOperations,
        mediaOperations = operations.mediaOperations,
        userLens = contextTools.userLens,
        render = contextTools.render
    )
)

const val POST_SEGMENT = "/posts"
const val CREATE_POST = "/new-post"
