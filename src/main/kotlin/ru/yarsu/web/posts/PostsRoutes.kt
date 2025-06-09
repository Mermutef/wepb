package ru.yarsu.web.posts

import org.http4k.core.*
import org.http4k.routing.*
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.posts.handlers.PostHandler
import ru.yarsu.web.posts.handlers.SavePostHandler
import ru.yarsu.web.posts.handlers.ShowEditPostFormHandler
import ru.yarsu.web.posts.handlers.SetPostStatusHandler
import ru.yarsu.web.posts.handlers.ShowNewPostFormHandler

fun postsRoutes(
    contextTools: ContextTools,
    operations: OperationsHolder,
    editorRoleFilter: Filter,
) = routes(
    CREATE_POST bind Method.GET to editorRoleFilter
        .then(
            ShowNewPostFormHandler(
                render = contextTools.render,
                hashtagsOperations = operations.hashtagOperations
            )
        ),
    CREATE_POST bind Method.POST to editorRoleFilter
        .then(
            SavePostHandler(
                postOperations = operations.postOperations,
                hashtagOperations = operations.hashtagOperations,
                mediaOperations = operations.mediaOperations,
                userLens = contextTools.userLens,
                render = contextTools.render
            )
        ),
    "/{id}" bind Method.GET to PostHandler(
        postsOperations = operations.postOperations,
        hashtagOperations = operations.hashtagOperations,
        userOperations = operations.userOperations,
        render = contextTools.render
    ),
    "/{id}$SET_STATUS" bind Method.POST to editorRoleFilter
        .then(
            SetPostStatusHandler(
                postOperations = operations.postOperations,
                userLens = contextTools.userLens
            )
        ),
    "/{id}/edit" bind Method.GET to ShowEditPostFormHandler(
        render = contextTools.render,
        userLens = contextTools.userLens,
        operations = operations,
    ),
    "/{id}/edit" bind Method.POST to SavePostHandler(
        postOperations = operations.postOperations,
        hashtagOperations = operations.hashtagOperations,
        mediaOperations = operations.mediaOperations,
        userLens = contextTools.userLens,
        render = contextTools.render
    ),
)

const val POST_SEGMENT = "/posts"
const val CREATE_POST = "/new-post"
const val SET_STATUS = "/set-status"
