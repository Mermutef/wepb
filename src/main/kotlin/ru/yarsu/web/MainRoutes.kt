package ru.yarsu.web

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.*
import org.http4k.template.ViewModel
import ru.yarsu.JWT_ISSUER
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.JWTTools
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.auth.AUTH_SEGMENT
import ru.yarsu.web.auth.authRouter
import ru.yarsu.web.comments.COMMENTS_SEGMENT
import ru.yarsu.web.comments.commentsRoutes
import ru.yarsu.web.common.handlers.GeneralPageHandler
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.filters.FiltersHolder
import ru.yarsu.web.filters.editorRoleFilter
import ru.yarsu.web.filters.roleFilter
import ru.yarsu.web.media.MEDIA_SEGMENT
import ru.yarsu.web.media.mediaRouter
import ru.yarsu.web.posts.POST_SEGMENT
import ru.yarsu.web.posts.postsRoutes
import ru.yarsu.web.profile.PROFILE_SEGMENT
import ru.yarsu.web.profile.admin.ADMIN_SEGMENT
import ru.yarsu.web.profile.admin.adminRoutes
import ru.yarsu.web.profile.moderator.MODERATOR_SEGMENT
import ru.yarsu.web.profile.moderator.moderatorRoutes
import ru.yarsu.web.profile.profileRoutes
import ru.yarsu.web.profile.user.USER
import ru.yarsu.web.profile.user.userRoutes
import ru.yarsu.web.profile.writer.WRITER_SEGMENT
import ru.yarsu.web.profile.writer.writerRoutes
import java.io.InputStream

@Suppress("LongParameterList")
private fun createMainRouter(
    contextTools: ContextTools,
    operations: OperationsHolder,
    config: AppConfig,
    jwtTools: JWTTools,
    writerRoleFilter: Filter,
    moderatorRoleFilter: Filter,
    adminRoleFilter: Filter,
    editorRoleFilter: Filter,
) = routes(
    "/" bind Method.GET to GeneralPageHandler(
        render = contextTools.render,
        postsOperations = operations.postOperations,
        hashtagsOperations = operations.hashtagOperations,
    ),
    MEDIA_SEGMENT bind mediaRouter(contextTools = contextTools, operations = operations),
    AUTH_SEGMENT bind authRouter(
        contextTools = contextTools,
        config = config,
        operations = operations,
        jwtTools = jwtTools
    ),
    ADMIN_SEGMENT bind adminRoleFilter
        .then(
            adminRoutes(
                contextTools = contextTools,
                operations = operations,
            )
        ),
    PROFILE_SEGMENT bind profileRoutes(
        contextTools = contextTools,
    ),
    USER bind userRoutes(
        contextTools = contextTools,
        operations = operations,
        config = config,
    ),
    WRITER_SEGMENT bind writerRoleFilter
        .then(
            writerRoutes(
                contextTools = contextTools,
                operations = operations
            )
        ),
    MODERATOR_SEGMENT bind moderatorRoleFilter
        .then(
            moderatorRoutes(
                contextTools = contextTools,
                operations = operations
            )
        ),
    POST_SEGMENT bind postsRoutes(
        contextTools = contextTools,
        operations = operations,
        editorRoleFilter = editorRoleFilter
    ),
    COMMENTS_SEGMENT bind commentsRoutes(operations, contextTools),
    "/static" bind static(ResourceLoader.Classpath("/ru/yarsu/public")),
)

fun createApp(
    operations: OperationsHolder,
    config: AppConfig,
): RoutingHttpHandler {
    val contexts = ContextTools(config.webConfig)
    val jwtTools = JWTTools(config.authConfig.secret, JWT_ISSUER)

    val moderatorRoleFilter = roleFilter(contexts.userLens, Role.MODERATOR)
    val writerRoleFilter = roleFilter(contexts.userLens, Role.WRITER)
    val adminRoleFilter = roleFilter(contexts.userLens, Role.ADMIN)
    val editorRoleFilter = editorRoleFilter(contexts.userLens)

    val filters = FiltersHolder(
        operations = operations,
        contextTools = contexts,
        config = config,
        jwtTools = jwtTools,
    )

    val app = createMainRouter(
        contextTools = contexts,
        operations = operations,
        config = config,
        jwtTools = jwtTools,
        writerRoleFilter = writerRoleFilter,
        moderatorRoleFilter = moderatorRoleFilter,
        editorRoleFilter = editorRoleFilter,
        adminRoleFilter = adminRoleFilter,
    )

    return filters.all.then(app)
}

infix fun BiDiBodyLens<ViewModel>.extract(viewModel: ViewModel?) =
    viewModel
        ?.let { Response(Status.OK).with(this of it) }
        ?: notFound

fun redirect(to: String = "/"): Response = Response(Status.FOUND).header("Location", to)

val notFound: Response = Response(Status.NOT_FOUND)

val internalServerError: Response = Response(Status.INTERNAL_SERVER_ERROR)

fun ok(body: String = "pong"): Response = Response(Status.OK).body(body)

fun ok(body: InputStream): Response = Response(Status.OK).body(body)
