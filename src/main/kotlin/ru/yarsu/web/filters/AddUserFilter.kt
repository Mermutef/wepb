package ru.yarsu.web.filters

import dev.forkhandles.result4k.Success
import org.http4k.core.*
import org.http4k.core.cookie.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.domain.tools.JWTTools
import ru.yarsu.web.cookies.clearGlobalCookie

class AddUserFilter(
    private val userLens: RequestContextLens<User?>,
    private val userOperations: UserOperationsHolder,
    private val jwtTools: JWTTools,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        { request: Request ->
            when (val jwt = request.cookie("auth")?.value) {
                is String -> when (val id = jwtTools.verifyToken(jwt)) {
                    is Success -> Success(
                        next(
                            request.with(
                                userLens of when (val result = userOperations.fetchUserByID(id.value.toInt())) {
                                    is Success -> result.value
                                    else -> null
                                }
                            )
                        )
                    )

                    else -> Success(next(request).clearGlobalCookie("auth"))
                }.value

                else -> next(request)
            }
        }
}
