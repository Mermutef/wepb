package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.*
import org.http4k.template.ViewModel
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.media.MEDIA_SEGMENT
import ru.yarsu.web.media.mediaRouter
import java.io.InputStream

@Suppress("LongParameterList")
private fun createMainRouter(
    contextTools: ContextTools,
    operations: OperationsHolder,
) = routes(
    "/" bind Method.GET to { _ -> ok("pong") },
    MEDIA_SEGMENT bind mediaRouter(contextTools = contextTools, operations = operations),
    "/static" bind static(ResourceLoader.Classpath("/ru/yarsu/public")),
)

fun createApp(
    operations: OperationsHolder,
    config: AppConfig,
): RoutingHttpHandler {
    val contexts = ContextTools(config.webConfig)

    val app = createMainRouter(
        operations = operations,
        contextTools = contexts,
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
