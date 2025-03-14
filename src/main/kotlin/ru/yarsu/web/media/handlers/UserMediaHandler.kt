package ru.yarsu.web.media.handlers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.lens.nonEmptyString
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.media.MediaOperationsHolder
import ru.yarsu.web.lenses.GeneralWebLenses.lensOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.ok

class UserMediaHandler(
    private val userLens: RequestContextLens<User?>,
    private val mediaOperations: MediaOperationsHolder,
) : HttpHandler {
    private val mediaTypeLens = Path.nonEmptyString().of("mediaType")
    val mapper = jacksonObjectMapper()

    override fun invoke(request: Request): Response {
        val user = userLens(request) ?: return notFound
        return lensOrNull(mediaTypeLens, request)
            ?.let {
                mediaOperations
                    .fetchMediaByUserAndMediaType(
                        user,
                        MediaType.valueOf(it.uppercase()),
                    )
                    .valueOrNull()
            }
            ?.let { fetchedMedia ->
                ok(mapper.writeValueAsString(fetchedMedia.map { it.filename }))
            } ?: notFound
    }
}
