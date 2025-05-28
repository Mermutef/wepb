package ru.yarsu.web.media

import org.http4k.core.Method.*
import org.http4k.routing.*
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.media.handlers.ExtractVideoPlayerLinkHandler
import ru.yarsu.web.media.handlers.MediaShareHandler
import ru.yarsu.web.media.handlers.UploadMediaHandler
import ru.yarsu.web.media.handlers.UserMediaHandler

fun mediaRouter(
    contextTools: ContextTools,
    operations: OperationsHolder,
) = routes(
    "/{filename}" bind GET to MediaShareHandler(operations.mediaOperations),
    UPLOAD bind POST to UploadMediaHandler(contextTools.userLens, operations.mediaOperations),
    "$USER_MEDIA/{mediaType}" bind GET to UserMediaHandler(contextTools.userLens, operations.mediaOperations),
    EXTRACT_PLAYER_LINK bind POST to ExtractVideoPlayerLinkHandler(),
)

const val MEDIA_SEGMENT = "/media"

const val UPLOAD = "/upload"
const val USER_MEDIA = "/user-media"
const val EXTRACT_PLAYER_LINK = "/extract-link"
