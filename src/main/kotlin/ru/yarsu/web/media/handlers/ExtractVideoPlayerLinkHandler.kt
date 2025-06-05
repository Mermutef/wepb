package ru.yarsu.web.media.handlers

import org.http4k.core.*
import ru.yarsu.web.lenses.GeneralWebLenses.from
import ru.yarsu.web.media.lenses.MediaLenses.videoLinkField
import ru.yarsu.web.media.lenses.MediaLenses.videoLinkForm
import ru.yarsu.web.ok

class ExtractVideoPlayerLinkHandler : HttpHandler {
    override fun invoke(request: Request): Response {
        val form = videoLinkForm from request
        if (form.errors.isNotEmpty()) {
            return ok("Error: Некорректная ссылка на проигрыватель")
        }
        return ok(videoLinkField from form)
    }
}
