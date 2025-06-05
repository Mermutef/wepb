package ru.yarsu.web.filters

import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.User
import ru.yarsu.web.auth.AUTH_SEGMENT
import ru.yarsu.web.profile.PROFILE_SEGMENT
import ru.yarsu.web.redirect

class AlreadyAuthFilter(
    private val userLens: RequestContextLens<User?>,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        { request: Request ->
            if (request.uri.toString().startsWith("$AUTH_SEGMENT/sign-in") ||
                request.uri.toString().startsWith("$AUTH_SEGMENT/sign-up")
            ) {
                userLens(request)?.let {
                    redirect("$PROFILE_SEGMENT/${it.login}")
                } ?: next(request)
            } else {
                next(request)
            }
        }
}
