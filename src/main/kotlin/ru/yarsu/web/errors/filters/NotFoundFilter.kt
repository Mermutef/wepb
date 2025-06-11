package ru.yarsu.web.errors.filters

import org.http4k.core.Filter
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.errors.models.NotFoundVM
import ru.yarsu.web.notFound

fun notFoundFilter(renderer: ContextAwareViewRender): Filter =
    Filter { next ->
        { request ->
            val response = next(request)
            if (response.status == Status.NOT_FOUND) {
                val model = NotFoundVM(request.uri.toString().substringBefore("?"))
                notFound.with(renderer(request) of model)
            } else {
                response
            }
        }
    }
