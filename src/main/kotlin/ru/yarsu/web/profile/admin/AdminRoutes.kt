package ru.yarsu.web.profile.admin

import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.profile.admin.handlers.ManageUsersHandler

fun adminRoutes(
    contextTools: ContextTools,
    operations: OperationsHolder,
) = routes(
    MANAGE_USERS bind GET to ManageUsersHandler(contextTools.render, operations, contextTools.userLens),
    MANAGE_USERS bind POST to ManageUsersHandler(contextTools.render, operations, contextTools.userLens),
)

const val ADMIN_SEGMENT = "/admin"

const val MANAGE_USERS = "/manage-users"
