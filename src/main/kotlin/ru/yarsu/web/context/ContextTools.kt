package ru.yarsu.web.context

import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens
import ru.yarsu.config.WebConfig
import ru.yarsu.domain.models.User
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.context.templates.createContextAwareTemplateRenderer

class ContextTools(
    config: WebConfig,
) {
    val appContexts = RequestContexts()
    val userLens: RequestContextLens<User?> =
        RequestContextKey
            .optional(appContexts, "user")
    val render = ContextAwareViewRender.withContentType(
        createContextAwareTemplateRenderer(config),
        TEXT_HTML
    ).associateContextLenses(
        mapOf(
            "user" to userLens,
        ),
    )
}
