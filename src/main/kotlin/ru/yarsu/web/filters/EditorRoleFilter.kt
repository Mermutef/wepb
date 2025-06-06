package ru.yarsu.web.filters

import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User

fun editorRoleFilter(userKey: RequestContextLens<User?>) =
    Filter { next: HttpHandler ->
        { request ->
            val user = userKey(request)
            if (user?.role == Role.WRITER || user?.role == Role.MODERATOR) {
                next(request)
            } else {
                Response(Status.NOT_FOUND)
            }
        }
    }
