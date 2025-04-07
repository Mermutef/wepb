package ru.yarsu.web

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.RequestContextLens
import org.http4k.routing.*
import org.http4k.template.TemplateRenderer
import org.http4k.template.ViewModel
import ru.yarsu.config.AppConfig
import ru.yarsu.web.lenses.GeneralWebLenses.userLens
import ru.yarsu.config.AuthConfig
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.domain.tools.JWTTools
import ru.yarsu.web.auth.AUTH_SEGMENT
import ru.yarsu.web.auth.authRouter
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.media.MEDIA_SEGMENT
import ru.yarsu.web.media.mediaRouter
import java.io.InputStream

@Suppress("LongParameterList")
private fun createMainRouter(
    renderer: TemplateRenderer,
    contextTools: ContextTools,
    operations: OperationsHolder,
    config: AppConfig,
    jwtTools: JWTTools,
    userLens: RequestContextLens<User?>
) = routes(
    "/" bind Method.GET to { _ -> ok("pong") },
    MEDIA_SEGMENT bind mediaRouter(contextTools = contextTools, operations = operations),
    AUTH_SEGMENT bind authRouter(contextTools, config, operations, jwtTools),
    "/static" bind static(ResourceLoader.Classpath("/ru/yarsu/public")),
)

fun createApp(
    renderer: TemplateRenderer,
    operations: OperationsHolder,
    config: AppConfig
): RoutingHttpHandler {
    val contexts = ContextTools(config.webConfig)
    val requestContext = RequestContexts()
    val userLens = userLens(requestContext)
    val jwtTools = JWTTools(config.authConfig.secret, "ru.yarsu")
    val app = createMainRouter(
        renderer = renderer,
        contextTools = contexts,
        operations = operations,
        config = config,
        jwtTools = jwtTools,
        userLens = userLens
    )

    return app
}

infix fun BiDiBodyLens<ViewModel>.extract(viewModel: ViewModel?) =
    viewModel
        ?.let { Response(Status.OK).with(this of it) }
        ?: notFound

fun redirect(to: String = "/"): Response = Response(Status.FOUND).header("Location", to)

val notFound: Response = Response(Status.NOT_FOUND)

fun ok(body: String): Response = Response(Status.OK).body(body)

fun ok(body: InputStream): Response = Response(Status.OK).body(body)
