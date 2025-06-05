package ru.yarsu.web.filters

import org.http4k.core.*
import org.http4k.filter.AnyOf
import org.http4k.filter.CorsPolicy
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ServerFilters
import ru.yarsu.config.AppConfig

fun corsFilter(config: AppConfig): Filter =
    ServerFilters.Cors(
        CorsPolicy(
            originPolicy = OriginPolicy.AnyOf(
                "http://localhost:${config.webConfig.port}", // allow dev server
                "https://${config.webConfig.domain}", // allow secure public host
                "http://${config.webConfig.domain}", // allow insecure public host
            ),
            headers = listOf("content-type", "range"), // allow another context-types and media headers in requests
            methods = listOf(Method.GET, Method.POST),
            credentials = true, // allow cookies with requests
        )
    )
