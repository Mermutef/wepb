package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.Status
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.errors.models.NotFoundVM
import ru.yarsu.web.extract

fun notFoundFilter(renderer: ContextAwareViewRender): Filter =
    Filter { next ->
        { request ->
            val response = next(request)
            if (response.status == Status.NOT_FOUND) {
                val model = NotFoundVM(request.uri.toString().substringBefore("?"))
                renderer(request) extract model
            } else {
                response
            }
        }
    }
