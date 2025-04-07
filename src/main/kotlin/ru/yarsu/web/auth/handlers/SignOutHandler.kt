package ru.yarsu.web.auth.handlers

import org.http4k.core.*
import ru.yarsu.web.cookies.clearGlobalCookie
import ru.yarsu.web.redirect

class SignOutHandler : HttpHandler {
    override fun invoke(request: Request): Response {
        return redirect().clearGlobalCookie("auth")
    }
}
