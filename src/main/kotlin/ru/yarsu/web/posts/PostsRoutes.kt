package ru.yarsu.web.posts

import org.http4k.core.*
import org.http4k.routing.*
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.posts.handlers.ShowNewPostFormHandler

fun postsRoutes(contextTools: ContextTools) =
    routes(
        CREATE_POST bind Method.GET to ShowNewPostFormHandler(
            contextTools.render,
        ),
    )

const val POST_SEGMENT = "/posts"
const val CREATE_POST = "/new-post"
