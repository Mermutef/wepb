package ru.yarsu.web.profile.admin

import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.db.users.UserOperations
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.web.auth.lenses.UserWebLenses
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.profile.admin.handlers.AdminHandler

fun adminRoutes(
    contextTools: ContextTools,
    operations: UserOperationsHolder,
) = routes(
    "$ADMIN_SEGMENT$LIST_ALL_USERS" bind Method.GET to AdminHandler(
        render = contextTools.render,
        userOperations = operations,
        userLens = contextTools.userLens,
    )
)

// Константы для путей
const val ADMIN_SEGMENT = "/admin"
const val LIST_ALL_USERS = "/users"
