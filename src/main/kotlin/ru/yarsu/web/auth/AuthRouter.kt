package ru.yarsu.web.auth

import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.routing.*
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.JWTTools
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.auth.handlers.ShowSignInFormHandler
import ru.yarsu.web.auth.handlers.ShowSignUpFormHandler
import ru.yarsu.web.auth.handlers.SignInHandler
import ru.yarsu.web.auth.handlers.SignOutHandler
import ru.yarsu.web.auth.handlers.SignUpHandler
import ru.yarsu.web.context.ContextTools

fun authRouter(
    contextTools: ContextTools,
    config: AppConfig,
    operations: OperationsHolder,
    jwtTools: JWTTools,
): RoutingHttpHandler =
    routes(
        SIGN_UP bind GET to ShowSignUpFormHandler(contextTools.render),
        SIGN_UP bind POST to SignUpHandler(
            render = contextTools.render,
            userOperations = operations.userOperations,
            jwtTools = jwtTools,
        ),
        SIGN_IN bind GET to ShowSignInFormHandler(contextTools.render),
        SIGN_IN bind POST to SignInHandler(
            render = contextTools.render,
            userOperations = operations.userOperations,
            config = config.authConfig,
            jwtTools = jwtTools,
        ),
        SIGN_OUT bind GET to SignOutHandler(),
    )

const val AUTH_SEGMENT = "/auth"
const val SIGN_UP = "/sign-up"
const val SIGN_IN = "/sign-in"
const val SIGN_OUT = "/sign-out"
