package ru.yarsu.web.media.handlers

import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import org.http4k.lens.Path
import org.http4k.lens.nonEmptyString
import ru.yarsu.domain.operations.media.MediaOperationsHolder
import ru.yarsu.web.lenses.GeneralWebLenses.lensOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.ok
import kotlin.math.max
import kotlin.math.min

class MediaShareHandler(private val mediaOperations: MediaOperationsHolder) : HttpHandler {
    private val fileNameLens = Path.nonEmptyString().of("filename")

    override fun invoke(request: Request): Response =
        lensOrNull(fileNameLens, request)
            ?.let { mediaOperations.fetchMediaWithMeta(it).valueOrNull() }
            ?.let { fetchedMedia ->
                val requestedRange = request.header("range")
                    ?.substringAfterLast("=")
                    ?.split("-")
                    ?.takeIf { it.size == 2 }
                    ?: listOf("0", fetchedMedia.content.lastIndex.toString())

                val startRange = max(requestedRange[0].toIntOrNull() ?: 0, 0)
                val endRange = min(
                    requestedRange[1].toIntOrNull() ?: fetchedMedia.content.lastIndex,
                    fetchedMedia.content.lastIndex
                )
                if (startRange > endRange) return notFound

                ok(fetchedMedia.content.slice(startRange..endRange).toByteArray().inputStream())
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Length", fetchedMedia.content.size.toString())
                    .header("Accept-Ranges", "bytes")
                    .header("Content-Range", "bytes $startRange-$endRange/${fetchedMedia.content.size}")
            } ?: notFound
}
