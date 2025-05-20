package ru.yarsu.web.profile

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.User
import ru.yarsu.web.auth.lenses.UserWebLenses.authorizeUserFromPath
import ru.yarsu.web.notFound
import ru.yarsu.web.ok
import ru.yarsu.web.profile.user.USER
import ru.yarsu.web.redirect

class RedirectToHandlers(
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return when (
            val user = request.authorizeUserFromPath(
                userLens = userLens,
            )
        ) {
            is Failure -> notFound

            is Success -> {
                // todo переход на страницы профилей в зависимости от роли
                when {
                    user.value.isAdmin() -> ok("pong")
                    user.value.isModerator() -> ok("pong")
                    user.value.isWriter() -> ok("pong")
                    user.value.isReader() -> redirect("$USER/${user.value.login}")
                    else -> notFound
                }
            }
        }
    }
}
