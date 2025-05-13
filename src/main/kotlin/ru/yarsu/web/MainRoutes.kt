package ru.yarsu.web

import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.*
import org.http4k.template.ViewModel
import ru.yarsu.JWT_ISSUER
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.domain.tools.JWTTools
import ru.yarsu.web.auth.AUTH_SEGMENT
import ru.yarsu.web.auth.authRouter
import ru.yarsu.web.common.handlers.HomeHandler
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.filters.AuthenticationFilter
import ru.yarsu.web.media.MEDIA_SEGMENT
import ru.yarsu.web.media.mediaRouter
import ru.yarsu.web.profile.PROFILE_SEGMENT
import ru.yarsu.web.profile.profileRoutes
import ru.yarsu.web.profile.user.USER
import ru.yarsu.web.profile.user.userRoutes
import java.io.InputStream

@Suppress("LongParameterList")
private fun createMainRouter(
    contextTools: ContextTools,
    operations: OperationsHolder,
    config: AppConfig,
    jwtTools: JWTTools,
) = routes(
    "/" bind Method.GET to HomeHandler(contextTools.render, contextTools.userLens),
    MEDIA_SEGMENT bind mediaRouter(contextTools = contextTools, operations = operations),
    AUTH_SEGMENT bind authRouter(
        contextTools = contextTools,
        config = config,
        operations = operations,
        jwtTools = jwtTools
    ),
    PROFILE_SEGMENT bind profileRoutes(
        contextTools = contextTools,
    ),
    USER bind userRoutes(
        contextTools = contextTools,
//        operations = operations,
    ),
    "/static" bind static(ResourceLoader.Classpath("/ru/yarsu/public")),
)

fun createApp(
    operations: OperationsHolder,
    config: AppConfig,
): RoutingHttpHandler {
    val contexts = ContextTools(config.webConfig)
    val jwtTools = JWTTools(config.authConfig.secret, JWT_ISSUER)
    val app = createMainRouter(
        contextTools = contexts,
        operations = operations,
        config = config,
        jwtTools = jwtTools
    )

    return ServerFilters
        .InitialiseRequestContext(contexts.appContexts)
        .then(
            AuthenticationFilter(
                userLens = contexts.userLens,
                userOperations = operations.userOperations,
                jwtTools = jwtTools
            )
        ).then(app)
}

infix fun BiDiBodyLens<ViewModel>.extract(viewModel: ViewModel?) =
    viewModel
        ?.let { Response(Status.OK).with(this of it) }
        ?: notFound

fun redirect(to: String = "/"): Response = Response(Status.FOUND).header("Location", to)

val notFound: Response = Response(Status.NOT_FOUND)

fun ok(body: String = "pong"): Response = Response(Status.OK).body(body)

fun ok(body: InputStream): Response = Response(Status.OK).body(body)
