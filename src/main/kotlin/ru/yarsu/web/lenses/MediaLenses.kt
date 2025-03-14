package ru.yarsu.web.lenses

import org.http4k.core.*
import org.http4k.lens.MultipartFormField
import org.http4k.lens.MultipartFormFile
import org.http4k.lens.Validator
import org.http4k.lens.multipartForm
import org.http4k.lens.nonBlankString
import ru.yarsu.domain.models.MediaType

object MediaLenses {
    val fileField = MultipartFormFile.required("media-file")

    val mediaTypeField = MultipartFormField
        .string()
        .nonBlankString()
        .map(
            { fromForm -> MediaType.valueOf(fromForm) },
            { toForm: MediaType -> toForm.toString() }
        ).required("mediaType")

    val mediaFileForm = Body.multipartForm(Validator.Feedback, fileField).toLens()
}
