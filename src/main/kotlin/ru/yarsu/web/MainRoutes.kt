package ru.yarsu.web

import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.*
import org.http4k.template.ViewModel
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.domain.tools.JWTTools
import ru.yarsu.web.auth.AUTH_SEGMENT
import ru.yarsu.web.auth.authRouter
import ru.yarsu.web.common.handlers.HomeHandler
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.lenses.GeneralWebLenses.userLens
import ru.yarsu.web.media.MEDIA_SEGMENT
import ru.yarsu.web.media.mediaRouter
import java.io.InputStream

@Suppress("LongParameterList")
private fun createMainRouter(
    contextTools: ContextTools,
    operations: OperationsHolder,
    config: AppConfig,
    jwtTools: JWTTools,
) = routes(
    "/" bind Method.GET to HomeHandler(contextTools.render),
    MEDIA_SEGMENT bind mediaRouter(contextTools = contextTools, operations = operations),
    AUTH_SEGMENT bind authRouter(
        contextTools = contextTools,
        config = config,
        operations = operations,
        jwtTools = jwtTools
    ),
    "/static" bind static(ResourceLoader.Classpath("/ru/yarsu/public")),
)

fun createApp(
    operations: OperationsHolder,
    config: AppConfig,
): RoutingHttpHandler {
    val contexts = ContextTools(config.webConfig)
    val requestContext = RequestContexts()
    val jwtTools = JWTTools(config.authConfig.secret, "ru.yarsu")
    val app = createMainRouter(
        contextTools = contexts,
        operations = operations,
        config = config,
        jwtTools = jwtTools
    )

    return ServerFilters.InitialiseRequestContext(contexts.appContexts).then(app)
}

infix fun BiDiBodyLens<ViewModel>.extract(viewModel: ViewModel?) =
    viewModel
        ?.let { Response(Status.OK).with(this of it) }
        ?: notFound

fun redirect(to: String = "/"): Response = Response(Status.FOUND).header("Location", to)

val notFound: Response = Response(Status.NOT_FOUND)

fun ok(body: String): Response = Response(Status.OK).body(body)

fun ok(body: InputStream): Response = Response(Status.OK).body(body)
