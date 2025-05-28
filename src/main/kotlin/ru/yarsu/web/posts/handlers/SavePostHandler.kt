package ru.yarsu.web.posts.handlers

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import ru.yarsu.web.ok

class SavePostHandler : HttpHandler {
    override fun invoke(request: Request): Response {
        return ok("pong")
    }
}
