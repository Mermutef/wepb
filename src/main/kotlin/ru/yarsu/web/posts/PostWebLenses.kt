package ru.yarsu.web.posts

import org.http4k.core.*
import org.http4k.lens.FormField
import org.http4k.lens.nonBlankString
import org.http4k.lens.nonEmptyString
import ru.yarsu.web.lenses.GeneralWebLenses.makeBodyLensForFields

object PostWebLenses {
    val titleField = FormField
        .nonEmptyString().nonBlankString()
        .required("title", "Необходимо ввести заголовок поста")

    val contentField = FormField
        .nonEmptyString().nonBlankString()
        .required("content", "Необходимо написать текст поста")

    val hashtagField = FormField
        .nonEmptyString().nonBlankString()
        .required("hashtag", "Введите хэжтэг поста")

    val eventDateField = FormField
        .nonEmptyString().nonBlankString()
        .required("eventDate", "Укажите дату мероприятия")

    // todo медиа + редактор

    /*val fileField = MultipartFormFile.required("file-h")
    val mediaFileForm = Body.multipartForm(Validator.Feedback, fileField).toLens()*/

    val formFieldAll = makeBodyLensForFields(
        titleField,
        contentField,
        hashtagField,
        eventDateField
    )
}
