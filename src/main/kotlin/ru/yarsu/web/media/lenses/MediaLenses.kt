package ru.yarsu.web.media.lenses

import org.http4k.core.Body
import org.http4k.lens.FormField
import org.http4k.lens.MultipartFormField
import org.http4k.lens.MultipartFormFile
import org.http4k.lens.Validator
import org.http4k.lens.multipartForm
import org.http4k.lens.nonBlankString
import org.http4k.lens.nonEmptyString
import ru.yarsu.domain.models.MediaType
import ru.yarsu.web.lenses.GeneralWebLenses

object MediaLenses {
    val fileField = MultipartFormFile.Companion.required("media-file")

    val mediaTypeField = MultipartFormField.Companion
        .string()
        .nonBlankString()
        .map(
            { fromForm -> MediaType.valueOf(fromForm) },
            { toForm: MediaType -> toForm.toString() }
        ).required("mediaType")

    val mediaFileForm = Body.Companion.multipartForm(Validator.Feedback, fileField).toLens()

    private val srcLinkPattern =
        """src=("https://(rutube.ru/play/embed/|vkvideo.ru/video_ext.php|vk.com/video_ext.php)(.*?)")""".toRegex()

    val videoLinkField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            { fromForm ->
                srcLinkPattern.find(fromForm)
                    ?.value
                    ?.removePrefix("src=")
                    ?.removeSurrounding("\"")
                    ?: throw IllegalArgumentException("")
            },
            { toForm -> toForm }
        )
        .required("videoPlayerCode", "Ссылка на видео проигрыватель должна быть непустой строкой")

    val videoLinkForm = GeneralWebLenses.makeBodyLensForFields(videoLinkField)
}
