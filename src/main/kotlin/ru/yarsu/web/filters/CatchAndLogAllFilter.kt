package ru.yarsu.web.filters

import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.slf4j.LoggerFactory
import ru.yarsu.MAIN_CLASS
import ru.yarsu.web.internalServerError

private val logger = LoggerFactory.getLogger(MAIN_CLASS)

fun catchAndLogExceptionsFilter(): Filter =
    ServerFilters.CatchAll { throwable ->
        logger
            .atError()
            .setMessage("\n${throwable.stackTraceToString()}")
            .log()
        internalServerError
    }
