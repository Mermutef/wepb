package ru.yarsu.web.posts.lenses

import org.http4k.core.*
import org.http4k.lens.BiDiMapping
import org.http4k.lens.FormField
import org.http4k.lens.map
import org.http4k.lens.nonBlankString
import org.http4k.lens.nonEmptyString
import ru.yarsu.domain.models.Post.Companion.MAX_PREVIEW_LENGTH
import ru.yarsu.domain.models.Post.Companion.MAX_TITLE_LENGTH
import ru.yarsu.web.lenses.GeneralWebLenses.makeBodyLensForFields
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object PostWebLenses {
    val titleField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { title: String ->
                    title.takeIf {
                        title.length in 1..MAX_TITLE_LENGTH
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        )
        .required("title", PostLensErrors.TITLE_NOT_CORRECT.errorText)

    val previewField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { preview: String ->
                    preview.takeIf {
                        preview.length in 1..MAX_PREVIEW_LENGTH
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        )
        .required("preview", PostLensErrors.PREVIEW_NOT_CORRECT.errorText)

    val contentField = FormField
        .nonEmptyString().nonBlankString()
        .required("content", PostLensErrors.DESCRIPTION_NOT_CORRECT.errorText)

    val hashtagField = FormField
        .nonEmptyString().nonBlankString()
        .required("hashtag", PostLensErrors.HASHTAG_NOT_CORRECT.errorText)

    val hashtagInputField = FormField.optional("hashtag-input")

    val eventDateField = FormField
        .map(
            BiDiMapping(
                asOut = { eventDate: String ->
                    ZonedDateTime.of(
                        LocalDateTime.parse(eventDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        ZoneId.systemDefault()
                    )
                },
                asIn = { eventDate: ZonedDateTime ->
                    eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                }
            )
        )
        .optional("eventDate", PostLensErrors.EVENT_DATE_NOT_CORRECT.errorText)

    val formFieldAll = makeBodyLensForFields(
        titleField,
        previewField,
        contentField,
        hashtagField,
        hashtagInputField,
        eventDateField
    )
}

enum class PostLensErrors(val errorText: String) {
    TITLE_NOT_CORRECT(
        "Заголовок должен быть не пустым и иметь длину не более 100 симоволов"
    ),
    PREVIEW_NOT_CORRECT(
        "Превью должно быть не пустым, а название картинки превью должно иметь длину не более 256 символов"
    ),
    DESCRIPTION_NOT_CORRECT(
        "Текст поста должен быть не пустым"
    ),
    HASHTAG_NOT_CORRECT(
        "Необходимо выбрать хэжтэг поста"
    ),
    HASHTAG_INPUT_NOT_CORRECT(
        "Необходимо ввести название нового хэжтэга"
    ),
    EVENT_DATE_NOT_CORRECT(
        "Некорректная дата времени проведения мероприятия"
    ),
}
