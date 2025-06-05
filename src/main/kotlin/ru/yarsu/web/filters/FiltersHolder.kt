package ru.yarsu.web.filters

import org.http4k.core.*
import org.http4k.filter.ServerFilters
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.accounts.JWTTools
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.ContextTools

class FiltersHolder(
    contextTools: ContextTools,
    operations: OperationsHolder,
    jwtTools: JWTTools,
    config: AppConfig,
) {
    val initContext = ServerFilters.InitialiseRequestContext(contextTools.appContexts)
    val notFoundFilter = notFoundFilter(contextTools.render)
    val authenticationFilter = AuthenticationFilter(
        userLens = contextTools.userLens,
        userOperations = operations.userOperations,
        jwtTools = jwtTools
    )
    val corsFilter = corsFilter(config)
    val redirectAlreadyAuthUsers = AlreadyAuthFilter(contextTools.userLens)
    val catchAndLogExceptionsFilter = catchAndLogExceptionsFilter()
    val serverErrorFilter = serverErrorFilter(contextTools.render, catchAndLogExceptionsFilter)

    val all = catchAndLogExceptionsFilter
        .then(corsFilter)
        .then(initContext)
        .then(notFoundFilter)
        .then(serverErrorFilter)
        .then(authenticationFilter)
        .then(redirectAlreadyAuthUsers)
}
