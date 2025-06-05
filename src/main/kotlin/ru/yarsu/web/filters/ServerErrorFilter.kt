package ru.yarsu.web.filters

import org.http4k.core.*
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.errors.models.ServerErrorVM
import ru.yarsu.web.extract

fun serverErrorFilter(
    renderer: ContextAwareViewRender,
    catchAndLogExceptionsFilter: Filter,
): Filter =
    Filter { next ->
        { request ->
            val response = catchAndLogExceptionsFilter.then(next)(request)
            if (response.status == Status.INTERNAL_SERVER_ERROR) {
                val model = ServerErrorVM()
                renderer(request) extract model
            } else {
                response
            }
        }
    }
